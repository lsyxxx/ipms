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
import org.springframework.context.support.MessageSourceAccessor;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractFlowService implements FlowBaseService {

    private final BizFlowRelationRepository bizFlowRelationRepository;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    /**
     * 流程中参数Key, value统一为Form对象
     * TODO: 因为流程中参数flowable会保存到数据库，考虑最小参数。
     */
    public static final String VAR_KEY = "form_";
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

    @Override
    public ProcessVo<Form> start(String bizType, UserView user, Form form) {
        log.info("启动流程 -- 业务: {}, 用户: {}", bizType, user.getName());
        setStartUser(user.getId());
        //1. start process
        ProcessInstance processInstance;
        String taskId;
        try {
            //启动流程的taskId就是processInstanceId
            processInstance = runtimeService.startProcessInstanceById(form.getProcDefId(), form.getBusinessKey(), Map.of(VAR_KEY, form));
            taskId = processInstance.getId();
            log.info("已启动流程 -- id: {}", processInstance.getProcessInstanceId());
        } catch (Exception e) {
            log.error("启动流程失败 -- 失败原因: {}",e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new BusinessException(messages.getMessage("AbstractFlowService.start"));
        }
        BizFlowRelation bizFlowRelation = initBizFlowRelation(form, processInstance.getProcessInstanceId(), user);
        bizFlowRelation = bizFlowRelationRepository.save(bizFlowRelation);
        ProcessVo<Form> process = new ProcessVo(bizFlowRelation, form);
        //update Form
        form.updateProcess(processInstance.getId(), taskId);
        clearAuthenticationId();
        return process;
    }

    /**
     * 申请：启动流程+完成申请task
     * @param user 执行任务的用户中
     */
    public void apply(String bizType, UserView user, Form form) {
        //启动
        ProcessVo vo = start(bizType, user, form);
        //完成申请
        completeTask(user, vo);
    }

    @Override
    public void completeTask(UserView user, ProcessVo vo) {
        log.info("开始执行完成任务 -- 执行用户: {}|{}, 流程实例id: {},  taskId: {}", user.getId(), user.getName(), vo.getProcessInstanceId(), vo.getTaskId());
        taskService.claim(vo.getTaskId(), user.getId());
        beforeComplete(user, vo);
        taskService.complete(vo.getTaskId());
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
     * @param processDefinitionId
     * @param highLightedActivities
     * @return
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
     * @param user
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
    public void deleteProcess(String processInstanceId) {
        log.info("-----------删除流程: {}", processInstanceId);

    }

    @Override
    public void cancelProcess(String processInstanceId) {
        log.info("-----------撤销流程: {}", processInstanceId);
    }

}
