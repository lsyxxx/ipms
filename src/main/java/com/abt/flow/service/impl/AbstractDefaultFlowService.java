package com.abt.flow.service.impl;

import com.abt.common.util.MessageUtil;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.Decision;
import com.abt.flow.model.FlowForm;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.service.FlowBaseService;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 流程操作默认实现
 *
 */
@Slf4j
public abstract class AbstractDefaultFlowService implements FlowBaseService {
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private RepositoryService repositoryService;
    private FlowableConstant flowableConstant;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();
    private FlowOperationLogService flowOperationLogService;


    public static final String DIAG_PNG = "png";


    protected AbstractDefaultFlowService(RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService, FlowableConstant flowableConstant, FlowOperationLogService flowOperationLogService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
        this.flowableConstant = flowableConstant;
        this.flowOperationLogService = flowOperationLogService;
    }


    /**
     * 一般审批包含角色
     * 包括：部门审批人(deptManager)，技术负责人(techManager)，总经理(ceo)，财务总监(fiManager)，税务(texOfficer)，会计(accountancy)，下一个审批人(nextAssignee)
     */
    public Map<String, Object> defaultAssignee(Map<String, Object> processVars, FlowForm form) {
        processVars.put(FlowableConstant.PV_DEPT_MANAGER, form.getDeptManager());
        processVars.put(FlowableConstant.PV_TECH_MANAGER, form.getTechManager());
        processVars.put(FlowableConstant.PV_CEO, form.getCeo());
        processVars.put(FlowableConstant.PV_FI_MANAGER, form.getFiManager());
        processVars.put(FlowableConstant.PV_TAX_OFFICER, form.getTaxOfficer());
        processVars.put(FlowableConstant.PV_ACCOUNTANCY, form.getAccountancy());
        processVars.put(FlowableConstant.PV_NEXT_ASSIGNEE, form.getNextAssignee());

        return processVars;
    }

    ProcessInstance getActiveProcessInstance(String procId) {
        return runtimeService.createProcessInstanceQuery().active().processInstanceId(procId).singleResult();
    }

    @Override
    public void rejectTask(UserView user, String procId) {
        log.info("开始执行[拒绝任务], 流程实例id: {}", procId);
        runtimeService.deleteProcessInstance(procId, messages.getMessage("common.flow.deleteReason.reject", user.simpleInfo()));
    }

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

    @Override
    public void deleteRunningProcess(String processInstanceId, String delReason, UserView user) {
        log.info("开始执行删除流程deleteProcess(), 流程实例id: {}, 删除原因: {}, 删除用户: {}", processInstanceId, delReason, user.simpleInfo());
        if (StringUtils.isBlank(delReason)) {
            delReason = messages.getMessage("comm.flow.deleteReason.default");
        }
        //verify
        verifyRunningProcess(processInstanceId, messages.getMessage("flow.service.AbstractDefaultFlowService.deleteRunningProcess.error"));
        runtimeService.deleteProcessInstance(processInstanceId, delReason);
    }

    @Override
    public void cancelRunningProcess(String processInstanceId, UserView user) {
        log.info("开始执行取消流程cancelProcess(), 流程实例id: {}", processInstanceId);
        verifyRunningProcess(processInstanceId, messages.getMessage("flow.service.AbstractDefaultFlowService.cancelProcess.error"));
        runtimeService.deleteProcessInstance(processInstanceId, MessageUtil.format("flow.service.AbstractDefaultFlowService.cancelProcess.delReason", user.simpleInfo()));

    }

    public void verifyRunningProcess(String procId, String errMsg) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().active().processInstanceId(procId).singleResult();
        if (processInstance == null) {
            log.error("没有查询到正在进行的流程实例 -- {}", procId);
            throw new BusinessException(errMsg);
        }
    }

    public void verifyRunningProcess(String procId) {
        verifyRunningProcess(procId);
    }

        @Override
    public Task getActiveTask(String processInstanceId, String errMsg) {
        Task activeTask = taskService.createTaskQuery().active().processInstanceId(processInstanceId).singleResult();
        if (activeTask == null) {
            log.error("流程[{}]未查询到正在进行的任务", processInstanceId);
            if (errMsg == null) {
                errMsg = MessageUtil.format("common.flow.noActiveTask", processInstanceId);
            }
            throw new BusinessException(errMsg);
        }
        return activeTask;
    }

    @Override
    public void check(UserView user, Decision result, String procId, String taskId) {
        log.info("开始执行一般性审批, 审批人：{}, 审批流程: {}, 审批结果: {}", user.simpleInfo(), procId, result);
        switch (result) {
            case Approve:
                log.info("流程 - {} 审批[通过], 准备进行下一个节点!", procId);
                completeTask(taskId);
                break;
            case Reject:
                log.info("流程 - {} 审批[拒绝], 终止流程!", procId);
                rejectTask(user, procId);
                break;
            default:
                log.warn("未知的审批结果 -- {}，流程实例id: {}", result.name(), procId);

        }
    }

    @Override
    public ProcessInstance start(UserView user, String procDefId, String businessKey, Map<String, Object> variblesMap) {
        log.info("开始执行启动流程. 启动用户: {}, 流程定义id: {}, 业务key: {}", user.simpleInfo(), procDefId, businessKey);
        Authentication.setAuthenticatedUserId(user.getId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDefId, businessKey, variblesMap);
        Authentication.setAuthenticatedUserId(null);
        log.info("启动流程成功! 流程实例id: {}", processInstance.getId());
        return processInstance;
    }

    @Override
    public void completeTask(String taskId) {
        taskService.complete(taskId);
    }

}
