package com.abt.wf.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
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

import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.STATE_DETAIL_ACTIVE;

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
        super(identityService, flowOperationLogService);
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
    public void afterTask(TripReimburseForm form) {

    }

    @Override
    public void apply(TripReimburseForm form) {
        //prepare
        TripReimburse common = form.getCommon();
        if (common == null) {
            throw new MissingRequiredParameterException("common data");
        }
        WorkFlowUtil.ensureProcessDefinitionKey(common);
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

        //record
    }

    public void validateApplyForm(TripReimburseForm form) {
        validateApplyFormCommonData(form);
        if (form.getItems() == null) {
            throw new BusinessException("请填写出差报销明细");
        }
        form.getItems().forEach(this::validateApplyFormItem);
    }

    public void validateApplyFormCommonData(TripReimburseForm form) {
        TripReimburse common = form.getCommon();
        ensureProperty(common.getDeptId(), "申请部门(deptId)");
        ensureProperty(common.getStaff(), "出差人员(staff)");
        ensureProperty(common.getReason(), "出差事由(reason)");
        ensureProperty(common.getCompany(), "所属公司(company)");
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

    public void saveForm(TripReimburseForm form) {
        //common
        TripReimburse common = form.getCommon();

        //items
        for (TripReimburse trip : form.getItems()) {
            //set process info
        }

    }

    @Override
    public void approve(TripReimburseForm form) {

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
    public boolean isApproveUser(ReimburseForm form) {
        return false;
    }

    @Override
    public String notifyLink(String id) {
        return null;
    }
}
