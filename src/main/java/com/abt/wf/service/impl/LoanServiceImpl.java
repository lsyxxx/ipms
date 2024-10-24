package com.abt.wf.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.service.CreditBookService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.Loan;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.LoanRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.common.model.ValidationResult;
import com.abt.wf.repository.LoanRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.LoanService;
import com.abt.wf.service.SignatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.SERVICE_LOAN;
import static com.abt.wf.config.Constants.SERVICE_PAY;
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
    private final SignatureService signatureService;

    private final IFileService fileService;
    private final HistoryService historyService;
    private final CreditBookService creditBookService;

    private final CreditAndDebitBook<Loan> creditAndDebitBook;

    public LoanServiceImpl(LoanRepository loanRepository, IdentityService identityService, @Qualifier("sqlServerUserService") UserService userService, TaskService taskService,
                           FlowOperationLogService flowOperationLogService, RepositoryService repositoryService,
                           RuntimeService runtimeService, BpmnModelInstance loanBpmnModelInstance, SignatureService signatureService, IFileService fileService, HistoryService historyService, CreditBookService creditBookService, CreditAndDebitBook<Loan> creditAndDebitBook) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService, signatureService);
        this.loanRepository = loanRepository;
        this.identityService = identityService;
        this.userService = userService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.loanBpmnModelInstance = loanBpmnModelInstance;
        this.signatureService = signatureService;
        this.fileService = fileService;
        this.historyService = historyService;
        this.creditBookService = creditBookService;
        this.creditAndDebitBook = creditAndDebitBook;
    }

    @Override
    public List<CreditBook> loadCreditBook() {
        return List.of();
    }

    @Override
    public void writeCreditBook(Loan biz) {
        log.info("写入资金流出记录 -- 借款申请：entityId: {}", biz.getId());
        CreditBook creditBook = CreditBook.create(biz);
        creditBook.setServiceName(SERVICE_LOAN);
        creditBookService.saveCreditBook(creditBook);
    }

    @Override
    public Loan loadBusiness(String businessId) {
        return loanRepository.findById(businessId).orElse(null);
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
    public List<String> createBriefDesc(Loan entity) {
        return List.of();
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

        if (StringUtils.isNotBlank(form.getPayLevel())) {
            entity.setPayLevel(form.getPayLevel());
        }
        entity.setCheckItemJson(form.getCheckItemJson());
        creditAndDebitBook.setCreditBookProperty(form, entity, entity.getCurrentTaskName());

    }

    @Override
    void setFileListJson(Loan entity, String json) {
        entity.setFileList(json);
    }

    @Override
    String getAttachmentJson(Loan form) {
        return form.getFileList();
    }

    @Override
    void clearEntityId(Loan entity) {
        entity.setId(null);
    }

    @Override
    public Loan load(String entityId) {
        final Loan loan = loanRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到业务实体(loan)"));
        setActiveTask(loan);
        return loan;
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
    public Page<Loan> findAllByQueryPageable(LoanRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Loan> page = loanRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<Loan> findMyApplyByQueryPageable(LoanRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Loan> page = loanRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<Loan> findMyTodoByQueryPageable(LoanRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Loan> page = loanRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<Loan> findMyDoneByQueryPageable(LoanRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Loan> page = loanRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Loan saveEntity(Loan loan) {
        return loanRepository.save(loan);
    }
}
