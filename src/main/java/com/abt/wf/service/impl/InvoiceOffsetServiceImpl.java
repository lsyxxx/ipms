package com.abt.wf.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.model.act.ActHiTaskInstance;
import com.abt.wf.model.act.ActRuTask;
import com.abt.wf.repository.InvoiceOffsetRepository;
import com.abt.wf.repository.InvoiceOffsetTaskRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.InvoiceOffsetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.common.util.QueryUtil.like;
import static com.abt.wf.config.Constants.SERVICE_INV_OFFSET;


/**
 * 发票冲账流程
 */
@Service
@Slf4j
public class InvoiceOffsetServiceImpl extends AbstractWorkflowCommonServiceImpl<InvoiceOffset, InvoiceOffsetRequestForm> implements InvoiceOffsetService {

    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;

    private final InvoiceOffsetRepository invoiceOffsetRepository;
    private final InvoiceOffsetTaskRepository invoiceOffsetTaskRepository;

    public InvoiceOffsetServiceImpl(IdentityService identityService, FlowOperationLogService flowOperationLogService, TaskService taskService,
                                    @Qualifier("sqlServerUserService") UserService userService, RepositoryService repositoryService, RuntimeService runtimeService, IdentityService identityService1, RepositoryService repositoryService1, RuntimeService runtimeService1, TaskService taskService1, FlowOperationLogService flowOperationLogService1, UserService userService1, InvoiceOffsetRepository invoiceOffsetRepository, InvoiceOffsetTaskRepository invoiceOffsetTaskRepository) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService1;
        this.repositoryService = repositoryService1;
        this.runtimeService = runtimeService1;
        this.taskService = taskService1;
        this.flowOperationLogService = flowOperationLogService1;
        this.userService = userService1;
        this.invoiceOffsetRepository = invoiceOffsetRepository;
        this.invoiceOffsetTaskRepository = invoiceOffsetTaskRepository;
    }

    @Override
    ValidationResult beforePreview(InvoiceOffset form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(InvoiceOffset form) {
        return form.getDecision();
    }

    @Override
    void passHandler(InvoiceOffset form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(InvoiceOffset form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(InvoiceOffset form) {

    }

    @Override
    public List<InvoiceOffset> findAllByCriteriaPageable(InvoiceOffsetRequestForm requestForm) {
        return List.of();

    }

    @Override
    public int countAllByCriteria(InvoiceOffsetRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<InvoiceOffset> findMyApplyByCriteriaPageable(InvoiceOffsetRequestForm requestForm) {
        InvoiceOffsetSpecification spec = new InvoiceOffsetSpecification();
        Specification<InvoiceOffset> cr = Specification.where(spec.beforeEndDate(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.createUsernameLike(requestForm))
                .and(spec.stateEqual(requestForm))
                .and(spec.entityIdLike(requestForm))
                .and(spec.contractNameLike(requestForm));

        return invoiceOffsetRepository.findAll(cr);
    }

    public Page<InvoiceOffset> findMyApplyByCriteriaPaged(InvoiceOffsetRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        InvoiceOffsetSpecification spec = new InvoiceOffsetSpecification();
        Specification<InvoiceOffset> cr = Specification.where(spec.beforeEndDate(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.createUsernameLike(requestForm))
                .and(spec.stateEqual(requestForm))
                .and(spec.entityIdLike(requestForm))
                .and(spec.contractNameLike(requestForm));

        Page<InvoiceOffset> all = invoiceOffsetRepository.findAll(cr, page);
        all.getContent().forEach(this::buildActiveTask);
        return all;
    }

    public void buildActiveTask(InvoiceOffset entity) {
        ActRuTask task = entity.getCurrentTask();
        if (task == null) {
            return;
        }
        entity.setCurrentTaskId(task.getId());
        entity.setCurrentTaskAssigneeId(task.getAssignee());
        entity.setCurrentTaskName(task.getName());
        entity.setCurrentTaskDefId(task.getTaskDefKey());
        entity.setCurrentTaskStartTime(task.getCreateTime());
        entity.setCurrentTaskAssigneeName(task.getAssigneeInfo().getName());
    }


    public void buildInvokedTask(InvoiceOffset entity) {
    }

    @Override
    public int countMyApplyByCriteria(InvoiceOffsetRequestForm requestForm) {
        return  0;
    }

    @Override
    public List<InvoiceOffset> findMyDoneByCriteriaPageable(InvoiceOffsetRequestForm requestForm) {
        return List.of();
    }

    @Override
    public int countMyDoneByCriteria(InvoiceOffsetRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<InvoiceOffset> findMyTodoByCriteria(InvoiceOffsetRequestForm requestForm) {
        return List.of();
    }

    @Override
    public int countMyTodoByCriteria(InvoiceOffsetRequestForm requestForm) {
        return 0;
    }

    @Override
    public InvoiceOffset saveEntity(InvoiceOffset entity) {
        return invoiceOffsetRepository.save(entity);
    }

    @Override
    public InvoiceOffset load(String entityId) {
        return invoiceOffsetRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到发票冲账申请单(id=" + entityId + ")"));
    }

    @Override
    public String getEntityId(InvoiceOffset entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_INV_OFFSET;
    }

    @Override
    public Map<String, Object> createVariableMap(InvoiceOffset form) {
        return form.createVariableMap();
    }

    @Override
    public String businessKey(InvoiceOffset form) {
        return SERVICE_INV_OFFSET;
    }

    @Override
    public List<UserTaskDTO> preview(InvoiceOffset form) {
        return List.of();
    }

    @Override
    public void ensureEntityId(InvoiceOffset form) {
        ensureProperty(form.getId(), "审批编号(id)");
    }

    @Override
    public String notifyLink(String id) {
        return "/wf/invoffset/detail/" + id ;
    }

    @Override
    public InvoiceOffset getEntityWithCurrentTask(String entityId) {
        InvoiceOffset load = this.load(entityId);
        setActiveTask(load);
        return load;
    }

    static class InvoiceOffsetSpecification extends CommonSpecifications<InvoiceOffsetRequestForm, InvoiceOffset> {

        public Specification<InvoiceOffset> contractNameLike(InvoiceOffsetRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getContractName())) {
                    return builder.like(root.get("contractName"), like(form.getContractName()));
                }
                return null;
            };
        }

    }
}
