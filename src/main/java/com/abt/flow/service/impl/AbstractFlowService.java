package com.abt.flow.service.impl;

import com.abt.common.util.MessageUtil;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.Form;
import com.abt.flow.model.ProcessState;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowBaseService;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractFlowService<T extends Form> implements FlowBaseService {

    public static final String DIAG_PNG = "png";
    protected final RuntimeService runtimeService;
    protected final TaskService taskService;
    protected final HistoryService historyService;
    protected final RepositoryService repositoryService;
    protected final FlowOperationLogService flowOperationLogService;
    protected final FlowableConstant flowableConstant;
    private final BizFlowRelationRepository bizFlowRelationRepository;
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public AbstractFlowService(BizFlowRelationRepository bizFlowRelationRepository, RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService, FlowOperationLogService flowOperationLogService, FlowableConstant flowableConstant) {
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
        this.flowOperationLogService = flowOperationLogService;
        this.flowableConstant = flowableConstant;
    }

    public ProcessVo<T> apply(String bizType, UserView user, ProcessVo<T> processVo) {
        log.info("申请流程 --- 业务: {}, 用户: {}", bizType, user.getName());

        processVo = start(bizType, user, processVo);
        String proc = processVo.getProcessInstanceId();
        TaskInfo activeTask = getActiveTask(proc);
        Assert.notNull(activeTask, "流程[" + proc + "] 没有正在进行的任务");
        taskService.claim(activeTask.getId(), user.getId());
        taskService.complete(activeTask.getId());

        T form = processVo.get();
        BizFlowRelation bizFlowRelation = initBizFlowRelation(form, proc, user);
        bizFlowRelation = bizFlowRelationRepository.save(bizFlowRelation);
        processVo.copyOf(bizFlowRelation, form);
        //update variables
        requiredExecutionVariables(processVo);
        runtimeService.setVariables(proc, processVo.getProcessVariables());

        log.info("申请流程成功:processInfo = {}", proc);

        return processVo;
    }


    /**
     * 启动流程
     *
     * @param bizType   业务类型
     * @param user      申请用户
     * @param processVo 流程对象。传入时必须：form, processVariables。在启动成功后根据BizFlowRelation生成完整的对象
     */
    public ProcessVo<T> start(String bizType, UserView user, ProcessVo<T> processVo) {
        log.info("启动流程 -- 业务: {}, 用户: {}", bizType, user.getName());
        setStartUser(user.getId());
        //1. start process
        ProcessInstance processInstance;
        T form = processVo.get();
//        initProcessVariables(customName(processVo));

        try {
            //启动流程的taskId就是processInstanceId
            processInstance = runtimeService.startProcessInstanceById(form.getProcDefId(), form.getBusinessKey(), processVo.getProcessVariables());
            log.info("已启动流程 -- id: {}", processInstance.getProcessInstanceId());
        } catch (Exception e) {
            log.error("启动流程失败 -- 失败原因: {}", e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new BusinessException(messages.getMessage("flow.service.AbstractFlowService.start"));
        }

        clearAuthenticationId();
        return processVo;
    }

    @Override
    public void completeTask(UserView user, ProcessVo vo) {
        log.info("开始执行[完成任务] -- 执行用户: {}|{}, 流程实例id: {}", user.getId(), user.getName(), vo.getProcessInstanceId());
        vo.setUser(user.getId());
        Task task = getActiveTask(vo.getProcessInstanceId());
        if (task == null) {
            log.error("流程实例: {} 没有待完成的Task", vo.getProcessInstanceId());
            throw new BusinessException(messages.getMessage("flow.service.FlowInfoServiceImpl.completeTask"));
        }
        beforeComplete(user, vo);

        taskService.complete(task.getId());

        //NEXT task
        task = getActiveTask(vo.getProcessInstanceId());
        taskService.setAssignee(task.getId(), vo.getNextAssignee());

        vo.updateBy(task);

        log.info("Task: {} - {} 完成! 下一个执行人: {}", task.getId(), task.getName(), vo.getNextAssignee());
    }


    /**
     * 完成任务前业务处理
     */
    abstract void beforeComplete(UserView user, ProcessVo vo);

    @Override
    public List<FlowOperationLog> getOperationLogs(String processInstanceId) {
        return flowOperationLogService.getByProcessInstanceId(processInstanceId);
    }

    @Override
    public InputStream getHighLightedTaskPngDiagram(String processInstanceId, String processDefinitionId) {
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery()
                .processDefinitionId(processDefinitionId)
                .processInstanceId(processInstanceId)
                .list();
        List<String> highLightedActivities = historicActivityInstances.stream().map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());

        return generatePngDiagram(processDefinitionId, highLightedActivities);
    }

    /**
     * 生成流程图，高亮节点，不高亮连线，不显示连线名称
     *
     * @param processDefinitionId   流程定义id
     * @param highLightedActivities 高亮节点
     * @return InputStream
     */
    private InputStream generatePngDiagram(String processDefinitionId, List<String> highLightedActivities) {
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        // 获取流程图输入流
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        return generator.generateDiagram(bpmnModel,
                DIAG_PNG,
                highLightedActivities,
                Collections.emptyList(),
                flowableConstant.getDiagramFont(),
                flowableConstant.getDiagramFont(),
                flowableConstant.getDiagramFont(),
                null,
                flowableConstant.getScaleFactor(),
                true
        );
    }

    /**
     * 设置启动用户
     */
    protected void setStartUser(String user) {
        Authentication.setAuthenticatedUserId(user);
    }

    protected void clearAuthenticationId() {
        Authentication.setAuthenticatedUserId(null);
    }

    private BizFlowRelation initBizFlowRelation(Form form, String processInstanceId, UserView user) {
        BizFlowRelation bizFlowRelation = new BizFlowRelation();
        bizFlowRelation.setProcDefId(form.getProcDefId());
        bizFlowRelation.setBizCategoryId(form.getBizId());
        bizFlowRelation.setBizCategoryCode(form.getBizCode());
        bizFlowRelation.setBusinessKey(form.getBusinessKey());
        bizFlowRelation.setStartDate(LocalDate.now());
        bizFlowRelation.setStarterId(user.getId());
        bizFlowRelation.setStarterName(user.getName());
        bizFlowRelation.setProcInstId(processInstanceId);
        bizFlowRelation.setState(ProcessState.Active.code());

        return bizFlowRelation;
    }

    @Override
    public void deleteProcess(String processInstanceId, String delReason) {
        log.info("-----------删除流程: {}", processInstanceId);
        runtimeService.deleteProcessInstance(processInstanceId, delReason);

    }

    @Override
    public void cancelProcess(String processInstanceId) {
        log.info("-----------撤销流程: {}", processInstanceId);
    }


    /**
     * 初始化流程参数
     * 在启动流程时(start)添加
     */
    public Map<String, Object> initProcessVariables(ProcessVo<T> vo) {
        HashMap<String, Object> vars = new HashMap<>() {{
//            put(FlowableConstant.PV_NEXT_ASSIGNEE, nextAssignee);
//            put(FlowableConstant.PV_PROCESS_DESC, processDesc);
            put(FlowableConstant.PV_CUSTOM_NAME, customName(vo));
        }};
        Map<String, Object> toAdd = customInitProcessVariables(vo);
        vars.putAll(toAdd);
        return vars;
    }

    /**
     * 执行过程中需要的参数
     *
     * @param procVars 流程参数map
     * @param form 表单
     */
    private void requiredExecutionVariables(Map<String, Object> procVars, String nextAssignee, Form form) {

        procVars.put(FlowableConstant.PV_FORM, form);

    }

    /**
     * 自定义流程名称
     *
     */
    abstract String customName(ProcessVo<T> vo);


    /**
     * 默认customName
     * @param starter 启动流程用户
     * @param startTime 启动时间
     * @param bizTypeName 流程类型名称
     */
    public String defaultCustomName(String starter, String startTime, String bizTypeName) {
        return String.format("[%s] %s %s", starter, bizTypeName, startTime);
    }


    /**
     * 自定义流程参数，在流程启动时添加
     * @return Map<String, Object> 需要添加的参数map
     */
    abstract Map<String, Object> customInitProcessVariables(ProcessVo<T> processVo);

    /**
     * 设置流程参数，需要:
     * 1. 更新vo中的processVariables
     * 2. 更新流程中的processVariables
     */
    abstract Map<String, Object> requiredExecutionVariables(ProcessVo<T> processVo);


    /**
     * 获取流程中正在进行的节点
     * @return Task 没有则返回null
     */
    @Override
    public Task getActiveTask(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime().desc().singleResult();
    }


    @Override
    public ProcessVo<T> check(UserView user, ProcessVo vo) {
        log.info("开始执行一般性审核check(). 流程实例id: {}, 审批结果: {}, 评论: {}", vo.getProcessInstanceId(), vo.getCurrentResult(), vo.getComment());
//        TaskInfo activeTask = getActiveTask(vo.getProcessInstanceId());
//        String taskId = activeTask.getId();
//        taskService.addComment(taskId, vo.getProcessInstanceId(), vo.getComment());
        vo.addApproval();
        switch (vo.getCurrentResult()) {
            case Approve:
                log.info("流程 - {} 审批[通过], 准备进行下一个节点!", vo.getProcessInstanceId());
                completeTask(user, vo);
                break;
            case Reject:
                log.info("流程 - {} 审批[拒绝], 准备终止流程!", vo.getProcessInstanceId());
                rejectTask(user, vo);
                break;
            default:
                log.error("审批结果参数 - {} 类型错误!", vo.getCurrentResult());
                throw new IllegalArgumentException(MessageUtil.format("flow.service.AbstractFlowService.check.error", vo.getCurrentResult().value()));
        }
        return vo;
    }

    @Override
    public void rejectTask(UserView user, ProcessVo vo) {
        log.error("开始执行[审批拒绝]rejectTask(), 审批用户[id,name]: {}, 流程实例id: {}", user.simpleInfo(), vo.getProcessInstanceId());

        runtimeService.deleteProcessInstance(vo.getProcessInstanceId(), vo.getComment());

    }



}
