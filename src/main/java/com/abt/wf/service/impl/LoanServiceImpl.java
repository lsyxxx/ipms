package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Loan;
import com.abt.wf.model.LoanRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.model.ValidationResult;
import com.abt.wf.repository.LoanRepository;
import com.abt.wf.repository.WorkflowTaskQueryRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.SERVICE_LOAN;
import static com.abt.wf.config.Constants.STATE_DETAIL_REJECT;

/**
 *
 */
@Service
@Slf4j
public class LoanServiceImpl extends AbstractWorkflowCommonServiceImpl<Loan, LoanRequestForm> implements LoanService {

    private final LoanRepository loanRepository;
    private final IdentityService identityService;
    private final UserService userService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final WorkflowTaskQueryRepository workflowTaskQueryRepository;
    private final BpmnModelInstance loanBpmnModelInstance;

    public LoanServiceImpl(LoanRepository loanRepository, IdentityService identityService, UserService userService, TaskService taskService,
                           FlowOperationLogService flowOperationLogService, RepositoryService repositoryService,
                           RuntimeService runtimeService, WorkflowTaskQueryRepository workflowTaskQueryRepository,
                           BpmnModelInstance loanBpmnModelInstance) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.loanRepository = loanRepository;
        this.identityService = identityService;
        this.userService = userService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.workflowTaskQueryRepository = workflowTaskQueryRepository;
        this.loanBpmnModelInstance = loanBpmnModelInstance;
    }

    @Override
    public List<Loan> findAllByCriteriaPageable(LoanRequestForm requestForm) {
        return List.of();
    }

    @Override
    public List<Loan> findMyApplyByCriteriaPageable(LoanRequestForm requestForm) {
        return List.of();
    }

    @Override
    public List<Loan> findMyDoneByCriteriaPageable(LoanRequestForm requestForm) {
        return List.of();
    }

    @Override
    public List<Loan> findMyTodoByCriteria(LoanRequestForm requestForm) {
        return List.of();
    }

    @Override
    public Map<String, Object> createVariableMap(Loan form) {
        return form.createVarMap();
    }

    @Override
    public String businessKey(Loan form) {
        return SERVICE_LOAN;
    }

    @Override
    public List<UserTaskDTO> preview(Loan form) {
        return this.commonPreview(form, createVariableMap(form), loanBpmnModelInstance, form.copyList());
    }

    @Override
    public void ensureEntityId(Loan form) {
        ensureProperty(form.getId(), "借款单审批编号(id)");
    }

    @Override
    public String notifyLink(String id) {
        return "/wf/loan/detail/" + id;
    }

    @Override
    ValidationResult beforePreview(Loan form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(Loan form) {
        return form.getDecision();
    }

    @Override
    void passHandler(Loan form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(Loan form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(),form.getId());
    }

    @Override
    void afterApprove(Loan form) {
    }

    @Override
    public Loan load(String entityId) {
        return loanRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到业务实体(loan)"));
    }

    @Override
    public String getEntityId(Loan entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_LOAN;
    }

    @Override
    public Loan saveEntity(Loan loan) {
        return loanRepository.save(loan);
    }
}
