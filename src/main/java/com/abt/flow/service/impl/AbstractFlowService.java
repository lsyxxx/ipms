package com.abt.flow.service.impl;

import com.abt.common.util.MessageUtil;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.Form;
import com.abt.flow.model.ProcessState;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.flow.service.FlowBaseService;
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
import org.springframework.context.support.MessageSourceAccessor;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractFlowService<T extends Form> implements FlowBaseService {

    private final BizFlowRelationRepository bizFlowRelationRepository;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public static final String DIAG_PNG = "png";

    protected final RuntimeService runtimeService;
    protected final TaskService taskService;
    protected final HistoryService historyService;
    protected final RepositoryService repositoryService;
    protected final FlowOperationLogService flowOperationLogService;

    protected final FlowableConstant flowableConstant;

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

        start(bizType, user, processVo);

        completeTask(user, processVo);


        return processVo;
    }


    /**
     * 启动流程
     * @param bizType 业务类型
     * @param user 申请用户
     * @param processVo 流程对象
     */
    protected ProcessVo<T> start(String bizType, UserView user, ProcessVo processVo) {
        log.info("启动流程 -- 业务: {}, 用户: {}", bizType, user.getName());
        setStartUser(user.getId());
        //1. start process
        ProcessInstance processInstance;
        T form = (T) processVo.get();
        String processInstanceId = "";
        initProcessVariables(processVo);

        try {
            //启动流程的taskId就是processInstanceId
            processInstance = runtimeService.startProcessInstanceById(form.getProcDefId(), form.getBusinessKey(), processVo.getProcessVariables());
            log.info("已启动流程 -- id: {}", processInstance.getProcessInstanceId());
        } catch (Exception e) {
            log.error("启动流程失败 -- 失败原因: {}",e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new BusinessException(messages.getMessage("flow.service.AbstractFlowService.start"));
        }
        BizFlowRelation bizFlowRelation = initBizFlowRelation(form, processInstanceId, user);
        bizFlowRelation = bizFlowRelationRepository.save(bizFlowRelation);
        processVo.copyOf(bizFlowRelation, form);

        //update variables
        requiredExecutionVariables(processVo);

        runtimeService.setVariables(processInstanceId, processVo.getProcessVariables());

        clearAuthenticationId();
        return processVo;
    }

    @Override
    public void completeTask(UserView user, ProcessVo vo) {
        log.info("开始执行完成任务 -- 执行用户: {}|{}, 流程实例id: {}", user.getId(), user.getName(), vo.getProcessInstanceId());
        vo.setUser(user.getId());
        Task task = getActiveTask(vo.getProcessInstanceId());
        if (task == null) {
            log.error("流程实例: {} 没有待完成的Task", vo.getProcessInstanceId());
            throw new BusinessException(messages.getMessage("flow.service.FlowInfoServiceImpl.completeTask"));
        }

        beforeComplete(user, vo);

        taskService.complete(vo.getTaskId());

        //NEXT task
        task = getActiveTask(vo.getProcessInstanceId());
        taskService.setAssignee(task.getId(), user.getId());

        vo.updateBy(task);

        log.info("Task: {} - {} 完成!", task.getId(), task.getName());
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
     * @param processDefinitionId 流程定义id
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
     */
    public Map<String, Object> initProcessVariables(String nextAssignee, String processDesc, String customName) {

        return new HashMap<>(){{
            put(FlowableConstant.PV_NEXT_ASSIGNEE, nextAssignee);
            put(FlowableConstant.PV_PROCESS_DESC, processDesc);
            put(FlowableConstant.PV_CUSTOM_NAME, customName);
        }};
    }

    /**
     * 执行过程中需要的参数
     * @param procVars
     * @param nextAssignee
     * @param form
     */
    private void requiredExecutionVariables(Map<String, Object> procVars, String nextAssignee, Form form) {

        procVars.put(FlowableConstant.PV_NEXT_ASSIGNEE, nextAssignee);
        procVars.put(FlowableConstant.PV_FORM, form);

    }

    /**
     * 自定义流程名称
     * @return
     */
    abstract String customName(String ...strings);


    /**
     * 启动流程前，初始化流程参数
     * @param processVo
     * @return Map<String, Object> processVariables
     */
    abstract Map<String, Object> initProcessVariables(ProcessVo processVo);

    /**
     * 设置流程参数，需要:
     * 1. 更新vo中的processVariables
     * 2. 更新流程中的processVariables
     */
    abstract Map<String, Object> requiredExecutionVariables(ProcessVo<T> processVo);


    /**
     * 获取流程中正在进行的节点
     * @param processInstanceId
     * @return Task 没有则返回null
     */
    @Override
    public Task getActiveTask(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime().desc().singleResult();
    }
}
