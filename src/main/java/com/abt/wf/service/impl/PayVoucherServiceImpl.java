package com.abt.wf.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.util.TimeUtil;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.service.CreditBookService;
import com.abt.finance.service.ICreditBook;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.common.model.ValidationResult;
import com.abt.wf.repository.PayVoucherRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PayVoucherService;
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

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_PAY_VOUCHER;

/**
 *
 */
@Service(DEF_KEY_PAY_VOUCHER)
@Slf4j
public class PayVoucherServiceImpl extends AbstractWorkflowCommonServiceImpl<PayVoucher, PayVoucherRequestForm> implements PayVoucherService {

    private final IdentityService identityService;
    private final UserService userService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final PayVoucherRepository payVoucherRepository;
    private final SignatureService signatureService;
    private final BpmnModelInstance payVoucherModelInstance;
    private final CreditAndDebitBook<PayVoucher> creditAndDebitBook;

    private final IFileService fileService;
    private final HistoryService historyService;
    private final CreditBookService creditBookService;


    public PayVoucherServiceImpl(IdentityService identityService, FlowOperationLogService flowOperationLogService, TaskService taskService,
                                 @Qualifier("sqlServerUserService") UserService userService, RepositoryService repositoryService, RuntimeService runtimeService, PayVoucherRepository payVoucherRepository, SignatureService signatureService,
                                 @Qualifier("payVoucherBpmnModelInstance") BpmnModelInstance payVoucherModelInstance, CreditAndDebitBook<PayVoucher> creditAndDebitBook, IFileService fileService, HistoryService historyService, CreditBookService creditBookService) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService, signatureService);
        this.identityService = identityService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.payVoucherRepository = payVoucherRepository;
        this.signatureService = signatureService;
        this.payVoucherModelInstance = payVoucherModelInstance;
        this.creditAndDebitBook = creditAndDebitBook;
        this.fileService = fileService;
        this.historyService = historyService;
        this.creditBookService = creditBookService;
    }

    @Override
    public Map<String, Object> createVariableMap(PayVoucher form) {
        return form.createVarMap();
    }

    @Override
    public String businessKey(PayVoucher form) {
        return Constants.SERVICE_PAY;
    }

    @Override
    public List<UserTaskDTO> preview(PayVoucher form) {
        return this.commonPreview(form,  form.createVarMap(), payVoucherModelInstance, form.copyList());
    }

    @Override
    public void ensureEntityId(PayVoucher form) {
        ensureProperty(form.getId(), "款项支付审批编号(id)");
    }

    @Override
    public boolean isApproveUser(PayVoucher form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return "/wf/pay/detail/" + id;
    }

    @Override
    public List<String> createBriefDesc(PayVoucher entity) {
        return List.of();
    }

    @Override
    ValidationResult beforePreview(PayVoucher form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(PayVoucher form) {
        return form.getDecision();
    }

    @Override
    void passHandler(PayVoucher form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(PayVoucher form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    public PayVoucher saveEntity(PayVoucher entity) {
        return payVoucherRepository.save(entity);
    }

    @Override
    void afterApprove(PayVoucher form) {

    }

    @Override
    void setApprovalResult(PayVoucher form, PayVoucher entity) {
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
    void setFileListJson(PayVoucher entity, String json) {
        entity.setOtherFileList(json);
    }

    @Override
    String getAttachmentJson(PayVoucher form) {
        return form.getOtherFileList();
    }

    @Override
    void clearEntityId(PayVoucher entity) {
        entity.setId(null);
    }

    @Override
    public PayVoucher load(String id) {
        final PayVoucher entity = payVoucherRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到款项支付单(id=" + id + ")"));
        setActiveTask(entity);
        return entity;
    }


    @Override
    public PayVoucher loadEntityWithCurrentTask(String id) {
        PayVoucher payVoucher = this.load(id);
        setActiveTask(payVoucher);
        return payVoucher;
    }

    @Override
    public String getEntityId(PayVoucher entity) {
        return  entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_PAY;
    }

    @Override
    public Page<PayVoucher> findAllByQueryPageable(PayVoucherRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PayVoucher> page = payVoucherRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<PayVoucher> findMyApplyByQueryPageable(PayVoucherRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PayVoucher> page = payVoucherRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<PayVoucher> findMyTodoByQueryPageable(PayVoucherRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PayVoucher> page = payVoucherRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<PayVoucher> findMyDoneByQueryPageable(PayVoucherRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PayVoucher> page = payVoucherRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public int countMyTodo(PayVoucherRequestForm requestForm) {
        return payVoucherRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public int countMyTodoByRequestForm(RequestForm requestForm) {
        return payVoucherRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public List<PayVoucher> findMyTodoList(RequestForm requestForm) {
        return payVoucherRepository.findUserTodoList(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), requestForm.getTaskDefKey());
    }

    @Override
    public PayVoucherRequestForm createRequestForm() {
        return new PayVoucherRequestForm();
    }

    @Override
    public List<CreditBook> loadCreditBook() {
        return List.of();
    }

    @Override
    public void writeCreditBook(PayVoucher biz) {
        log.info("写入资金流出记录 -- 支付申请：entityId: {}", biz.getId());
        CreditBook creditBook = CreditBook.create(biz);
        creditBook.setServiceName(SERVICE_PAY);
        creditBookService.saveCreditBook(creditBook);
    }

    @Override
    public PayVoucher loadBusiness(String businessId) {
        return payVoucherRepository.findById(businessId).orElse(null);
    }

    static class PayVoucherSpecifications extends CommonSpecifications<PayVoucherRequestForm, PayVoucher> {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称 合同编号
        public Specification<PayVoucher> contractNoLike(PayVoucherRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getContractNo())) {
                    return builder.like(root.get("contractNo"), form.getContractNo());
                }
                return null;
            };
        }

        public Specification<PayVoucher> contractNameLike(PayVoucherRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getContractName())) {
                    return builder.like(root.get("contractName"), form.getContractName());
                }
                return null;
            };
        }
    }
}
