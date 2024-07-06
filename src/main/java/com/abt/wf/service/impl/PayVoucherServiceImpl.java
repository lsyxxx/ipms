package com.abt.wf.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.common.model.ValidationResult;
import com.abt.wf.repository.PayVoucherRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PayVoucherService;
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

    private final BpmnModelInstance payVoucherModelInstance;

    public PayVoucherServiceImpl(IdentityService identityService, FlowOperationLogService flowOperationLogService, TaskService taskService,
                                 @Qualifier("sqlServerUserService") UserService userService, RepositoryService repositoryService, RuntimeService runtimeService, PayVoucherRepository payVoucherRepository,
                                 @Qualifier("payVoucherBpmnModelInstance") BpmnModelInstance payVoucherModelInstance) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.payVoucherRepository = payVoucherRepository;
        this.payVoucherModelInstance = payVoucherModelInstance;
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
    }

    @Override
    void clearEntityId(PayVoucher entity) {
        entity.setId(null);
    }

    @Override
    public PayVoucher load(String id) {
        return payVoucherRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到款项支付单(id=" + id + ")"));
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
