package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.Loan;
import com.abt.wf.model.LoanRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.common.model.ValidationResult;
import com.abt.wf.repository.LoanRepository;
import com.abt.wf.repository.LoanTaskRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.SERVICE_LOAN;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_INVOFFSET;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_LOAN;

/**
 *
 */
@Service(DEF_KEY_LOAN)
@Slf4j
public class LoanServiceImpl extends AbstractWorkflowCommonServiceImpl<Loan, LoanRequestForm> implements LoanService {

    private final LoanRepository loanRepository;
    private final IdentityService identityService;
    private final UserService userService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final BpmnModelInstance loanBpmnModelInstance;
    private final LoanTaskRepository loanTaskRepository;

    public LoanServiceImpl(LoanRepository loanRepository, IdentityService identityService, @Qualifier("sqlServerUserService") UserService userService, TaskService taskService,
                           FlowOperationLogService flowOperationLogService, RepositoryService repositoryService,
                           RuntimeService runtimeService, BpmnModelInstance loanBpmnModelInstance, LoanTaskRepository loanTaskRepository) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.loanRepository = loanRepository;
        this.identityService = identityService;
        this.userService = userService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.loanBpmnModelInstance = loanBpmnModelInstance;
        this.loanTaskRepository = loanTaskRepository;
    }

    @Override
    public List<Loan> findAllByCriteriaPageable(LoanRequestForm requestForm) {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 支付方式,项目，借款部门
        requestForm.forcePaged();
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        LoanSpecifications spec = new LoanSpecifications();
        Specification<Loan> criteria = Specification.where(spec.beforeEndDate(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.createUsernameLike(requestForm))
                .and(spec.stateEqual(requestForm))
                .and(spec.entityIdLike(requestForm))
                .and(spec.payTypeEqual(requestForm))
                .and(spec.projectLike(requestForm))
                .and(spec.deptIdEqual(requestForm))
                .and(spec.createUseridEqual(requestForm));
        return loanRepository.findAll(criteria, pageable).getContent();
    }

    @Override
    public int countAllByCriteria(LoanRequestForm requestForm) {
        LoanSpecifications spec = new LoanSpecifications();
        Specification<Loan> criteria = Specification.where(spec.beforeEndDate(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.createUsernameLike(requestForm))
                .and(spec.stateEqual(requestForm))
                .and(spec.entityIdLike(requestForm))
                .and(spec.payTypeEqual(requestForm))
                .and(spec.projectLike(requestForm))
                .and(spec.deptIdEqual(requestForm))
                .and(spec.createUseridEqual(requestForm));
        return (int) loanRepository.count(criteria);
    }

    static class LoanSpecifications extends CommonSpecifications<LoanRequestForm, Loan> {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 支付方式,项目，借款部门
        public Specification<Loan> payTypeEqual(LoanRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getPayType())) {
                    return builder.equal(root.get("payType"), form.getPayType());
                }
                return null;
            };
        }

        public Specification<Loan> projectLike(LoanRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getProject())) {
                    return builder.like(root.get("project"), form.getProject());
                }
                return null;
            };
        }
        public Specification<Loan> deptIdEqual(LoanRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getDeptId())) {
                    return builder.like(root.get("deptId"), form.getDeptId());
                }
                return null;
            };
        }
    }

    @Override
    public List<Loan> findMyApplyByCriteriaPageable(LoanRequestForm requestForm) {
        return loanTaskRepository.findUserApplyList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getPayType(),
                requestForm.getProject(), requestForm.getDeptId());
    }

    @Override
    public int countMyApplyByCriteria(LoanRequestForm requestForm) {
        return this.countAllByCriteria(requestForm);
    }

    @Override
    public List<Loan> findMyDoneByCriteriaPageable(LoanRequestForm requestForm) {
        return loanTaskRepository.findDoneList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getPayType(),
                requestForm.getProject(), requestForm.getDeptId());
    }

    @Override
    public int countMyDoneByCriteria(LoanRequestForm requestForm) {
        return loanTaskRepository.countDoneList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getPayType(),
                requestForm.getProject(), requestForm.getDeptId());
    }

    @Override
    public List<Loan> findMyTodoByCriteria(LoanRequestForm requestForm) {
        return loanTaskRepository.findTodoList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getPayType(),
                requestForm.getProject(), requestForm.getDeptId());
    }

    @Override
    public int countMyTodoByCriteria(LoanRequestForm requestForm) {
        return loanTaskRepository.countTodoList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getPayType(),
                requestForm.getProject(), requestForm.getDeptId());
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
    public boolean isApproveUser(Loan form) {
        return this.doIsApproveUser(form);
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
    void setApprovalResult(Loan form, Loan entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    public Loan load(String entityId) {
        return loanRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到业务实体(loan)"));
    }

    @Override
    public Loan loadWithActiveTask(String entityId) {
        final Loan entity = this.load(entityId);
        final Task task = taskService.createTaskQuery().active().processInstanceId(entity.getProcessInstanceId()).singleResult();
        if (task != null) {
            setActiveTask(entity);
        }
        return entity;
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
