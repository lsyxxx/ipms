package com.abt.wf.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.TripRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.TripReimburseService;
import com.abt.wf.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.STATE_DETAIL_ACTIVE;
import static com.abt.wf.config.Constants.STATE_DETAIL_APPLY;

/**
 *
 */

@Service
public class TripReimburseServiceImpl extends AbstractWorkflowCommonServiceImpl<TripReimburseForm> implements TripReimburseService {
    private final FlowOperationLogService flowOperationLogService;

    private final RuntimeService runtimeService;

    private final RepositoryService repositoryService;

    private final TaskService taskService;

    private final TripRepository tripRepository;


    public TripReimburseServiceImpl(FlowOperationLogService flowOperationLogService, IdentityService identityService, RuntimeService runtimeService, RepositoryService repositoryService, TaskService taskService, TripRepository tripRepository) {
        super(identityService, flowOperationLogService, taskService);
        this.flowOperationLogService = flowOperationLogService;
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
        this.taskService = taskService;
        this.tripRepository = tripRepository;
    }

    @Override
    public Map<String, Object> createVariableMap(TripReimburseForm form) {
        return form.variableMap();
    }

    @Override
    public String businessKey(TripReimburseForm form) {
        return Constants.SERVICE_TRIP;
    }

    @Override
    public Map<String, Object> getVariableMap() {
        return null;
    }


    @Override
    public void apply(TripReimburseForm form) {
        //validate
        validateApplyForm(form);
        TripReimburse common = form.getCommon();
        WorkFlowUtil.ensureProcessDefinitionKey(common);
        //prepare

        Map<String, Object> variableMap = this.createVariableMap(form);

        //start instance
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(common.getProcessDefinitionKey()).latestVersion().active().singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(common.getProcessDefinitionKey(), businessKey(form), variableMap);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        task.setAssignee(common.getCreateUserid());
        taskService.setAssignee(task.getId(), common.getCreateUserid());
        taskService.complete(task.getId());

        //save entity(s)
        common.setProcessDefinitionId(processDefinition.getId());
        common.setProcessInstanceId(processInstance.getId());
        common.setProcessState(HistoricProcessInstance.STATE_ACTIVE);
        common.setBusinessState(STATE_DETAIL_ACTIVE);

        beforePersist(form);
        saveForm((form));
        runtimeService.setVariable(processInstance.getId(), Constants.VAR_KEY_ENTITY, form.getCommon().getId());

        //record
        FlowOperationLog optLog = FlowOperationLog.applyLog(form.getCommon().getCreateUserid(), form.getCommon().getCreateUsername(), form.getCommon(), task, form.getCommon().getId());
        optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
        optLog.setTaskResult(STATE_DETAIL_APPLY);
        flowOperationLogService.saveLog(optLog);
    }

    public void validateApplyForm(TripReimburseForm form) {
        validateApplyFormCommonData(form);
        if (CollectionUtils.isEmpty(form.getItems())) {
            throw new BusinessException("请填写出差报销明细");
        }
        form.getItems().forEach(this::validateApplyFormItem);
    }

    public void validateApplyFormCommonData(TripReimburseForm form) {
        TripReimburse common = form.getCommon();
        if (common == null) {
            throw new BusinessException("请填写必要信息");
        }
        ensureProperty(common.getDeptId(), "申请部门(deptId)");
        ensureProperty(common.getStaff(), "出差人员(staff)");
        ensureProperty(common.getReason(), "出差事由(reason)");
        ensureProperty(common.getCompany(), "所属公司(company)");
        ensureProperty(common.getPayeeId(), "领款人(payeeId)");
    }

    public void validateApplyFormItem(TripReimburse item) {
        if (item == null) {
            return;
        }
        if (item.getTripStartDate() == null || item.getTripEndDate() == null) {
            throw new MissingRequiredParameterException("起止日期");
        }
        if (StringUtils.isBlank(item.getTripOrigin()) || StringUtils.isBlank(item.getTripArrival())) {
            throw new MissingRequiredParameterException("起讫地点");
        }
        ensureProperty(item.getTransportation(), "交通工具(transportation)");
        if (item.getTransExpense() == null) {
            throw new MissingRequiredParameterException("交通费(transExpense)");
        }
    }

    public void ensureProperty(String prop, String msg) {
        if (StringUtils.isBlank(prop)) {
            throw new MissingRequiredParameterException(msg);
        }
    }

    public void beforePersist(TripReimburseForm form) {
        //common: dept,staff,...
        TripReimburse common = form.getCommon();
        //items
        for (TripReimburse trip : form.getItems()) {
            trip.copyProcessData(common);
            trip.sumItem();
        }
    }

    public void saveForm(TripReimburseForm form) {
        form.setCommon(tripRepository.save(form.getCommon()));
        final String rootId = form.getCommon().getId();
        form.getItems().forEach(i -> {
            i.setRootId(rootId);
            tripRepository.save(i);
        });

    }

    @Override
    public void revoke(String entityId) {

    }

    @Override
    public void delete(String entityId) {

    }

    @Override
    public List<UserTaskDTO> preview(TripReimburseForm form) {
        return null;
    }

    @Override
    public List<FlowOperationLog> processRecord(String entityId) {
        return null;
    }

    @Override
    public void ensureEntityId(TripReimburseForm form) {

    }

    @Override
    public String notifyLink(String id) {
        return null;
    }

    @Override
    String getDecision(TripReimburseForm form) {
        return null;
    }

    @Override
    public void passHandler(TripReimburseForm form) {
        //completeTask
        taskService.complete(form.getCurrentTaskId());
        //log
        final FlowOperationLog optLog = FlowOperationLog.create(form.getSubmitUserid(), form.getSubmitUsername(), form);

    }

    @Override
    public void rejectHandler(TripReimburseForm form) {

    }

    @Override
    void afterApprove(TripReimburseForm form) {

    }
}
