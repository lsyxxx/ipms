package com.abt.wf.service.impl;

import com.abt.common.util.TokenUtil;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.model.ValidationResult;
import com.abt.wf.repository.PayVoucherRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PayVoucherService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.STATE_DETAIL_ACTIVE;
import static com.abt.wf.config.Constants.STATE_DETAIL_APPLY;

/**
 *
 */
@Service
@Slf4j
public class PayVoucherServiceImpl extends AbstractWorkflowCommonServiceImpl<PayVoucher> implements PayVoucherService {

    private final IdentityService identityService;
    private final UserService userService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final PayVoucherRepository payVoucherRepository;

    public PayVoucherServiceImpl(IdentityService identityService, FlowOperationLogService flowOperationLogService, TaskService taskService,
                                 @Qualifier("sqlServerUserService") UserService userService, RepositoryService repositoryService, RuntimeService runtimeService, PayVoucherRepository payVoucherRepository) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.payVoucherRepository = payVoucherRepository;
    }

    @Override
    public List<PayVoucher> findAllByCriteriaPageable(PayVoucherRequestForm requestForm) {
        return List.of();
    }

    @Override
    public List<PayVoucher> findMyApplyByCriteriaPageable(PayVoucherRequestForm requestForm) {
        return List.of();
    }

    @Override
    public List<PayVoucher> findMyDoneByCriteriaPageable(PayVoucherRequestForm requestForm) {
        return List.of();
    }

    @Override
    public List<PayVoucher> findMyTodoByCriteria(PayVoucherRequestForm requestForm) {
        return List.of();
    }

    @Override
    public Map<String, Object> createVariableMap(PayVoucher form) {
        return Map.of();
    }

    @Override
    public String businessKey(PayVoucher form) {
        return Constants.SERVICE_PAY;
    }

    @Override
    public void apply(PayVoucher form) {
        //-- validate
        WorkFlowUtil.ensureProcessDefinitionKey(form);

        //-- prepare
        final Map<String, Object> variableMap = form.createVarMap();

        //-- start instance
        final Task applyTask = this.startProcessAndApply(form, variableMap, Constants.SERVICE_PAY);

        //-- save entity
        final PayVoucher entity = payVoucherRepository.save(form);
        runtimeService.setVariable(form.getProcessInstanceId(), Constants.VAR_KEY_ENTITY, entity.getId());

        //-- record
        FlowOperationLog optLog = FlowOperationLog.applyLog(form.getCreateUserid(), form.getCreateUsername(), form, applyTask, entity.getId());
        optLog.setTaskDefinitionKey(applyTask.getTaskDefinitionKey());
        optLog.setTaskResult(STATE_DETAIL_APPLY);
        flowOperationLogService.saveLog(optLog);
    }

    @Override
    public void revoke(String entityId) {

    }

    @Override
    public void delete(String entityId) {
        final ValidationResult validationResult = this.deleteValidate(entityId, TokenUtil.getUseridFromAuthToken());
        if (validationResult.isPass()) {
            runtimeService.deleteProcessInstance(entityId, STATE_DETAIL_APPLY);
        }
    }

    @Override
    public List<UserTaskDTO> preview(PayVoucher form) {
        return List.of();
    }

    @Override
    public void ensureEntityId(PayVoucher form) {
        ensureProperty(form.getId(), "款项支付审批编号(id)");
    }

    @Override
    public String notifyLink(String id) {
        return "";
    }

    @Override
    ValidationResult beforePreview(PayVoucher form) {
        return null;
    }

    @Override
    String getDecision(PayVoucher form) {
        return "";
    }

    @Override
    void passHandler(PayVoucher form) {

    }

    @Override
    void rejectHandler(PayVoucher form) {

    }

    @Override
    void afterApprove(PayVoucher form) {

    }
}
