package com.abt.wf.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.model.User;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.entity.act.ActRuTask;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.InvoiceOffsetRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.InvoiceOffsetService;
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

import static com.abt.common.util.QueryUtil.like;
import static com.abt.wf.config.Constants.SERVICE_INV_OFFSET;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_INVOFFSET;


/**
 * 发票冲账流程
 */
@Service(DEF_KEY_INVOFFSET)
@Slf4j
public class InvoiceOffsetServiceImpl extends AbstractWorkflowCommonServiceImpl<InvoiceOffset, InvoiceOffsetRequestForm> implements InvoiceOffsetService {

    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;
    private final SignatureService signatureService;

    private final InvoiceOffsetRepository invoiceOffsetRepository;

    private final BpmnModelInstance invoiceOffsetBpmnModelInstance;

    private final IFileService fileService;
    private final HistoryService historyService;

    public InvoiceOffsetServiceImpl(IdentityService identityService, FlowOperationLogService flowOperationLogService, TaskService taskService,
                                    @Qualifier("sqlServerUserService") UserService userService, RepositoryService repositoryService, RuntimeService runtimeService, SignatureService signatureService,
                                    InvoiceOffsetRepository invoiceOffsetRepository,
                                    @Qualifier("invoiceOffsetBpmnModelInstance") BpmnModelInstance invoiceOffsetBpmnModelInstance, IFileService fileService, HistoryService historyService) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService, signatureService);
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.signatureService = signatureService;
        this.invoiceOffsetRepository = invoiceOffsetRepository;
        this.invoiceOffsetBpmnModelInstance = invoiceOffsetBpmnModelInstance;
        this.fileService = fileService;
        this.historyService = historyService;
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
    void setApprovalResult(InvoiceOffset form, InvoiceOffset entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    void setFileListJson(InvoiceOffset entity, String json) {
        entity.setFileList(json);
    }

    @Override
    String getAttachmentJson(InvoiceOffset form) {
        return form.getFileList();
    }

    @Override
    void clearEntityId(InvoiceOffset entity) {
        entity.setId(null);
    }


    @Override
    public Page<InvoiceOffset> findAllByCriteria(InvoiceOffsetRequestForm requestForm) {
        requestForm.forcePaged();
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        InvoiceOffsetSpecification spec = new InvoiceOffsetSpecification();
        Specification<InvoiceOffset> criteria = Specification.where(spec.beforeEndDate(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.createUsernameLike(requestForm))
                .and(spec.stateEqual(requestForm))
                .and(spec.entityIdLike(requestForm))
                .and(spec.createUseridEqual(requestForm));
        final Page<InvoiceOffset> all = invoiceOffsetRepository.findAll(criteria, pageable);
        all.getContent().forEach(this::buildActiveTask);
        return all;
    }

    @Override
    public Page<InvoiceOffset> findAllByQueryPageable(InvoiceOffsetRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceOffset> findMyTodoByQueryPageable(InvoiceOffsetRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceOffset> findMyDoneByQueryPageable(InvoiceOffsetRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public int countMyTodo(InvoiceOffsetRequestForm requestForm) {
        return invoiceOffsetRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public int countMyTodoByRequestForm(RequestForm requestForm) {
        return invoiceOffsetRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public List<InvoiceOffset> findMyTodoList(RequestForm requestForm) {
        final List<InvoiceOffset> list = invoiceOffsetRepository.findUserTodoList(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), requestForm.getTaskDefKey());
        list.forEach(this::buildActiveTask);
        return list;
    }

    @Override
    public InvoiceOffsetRequestForm createRequestForm() {
        return new InvoiceOffsetRequestForm();
    }

    @Override
    public Page<InvoiceOffset> findMyApplyByQueryPageable(InvoiceOffsetRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
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
        return this.commonPreview(form, createVariableMap(form), invoiceOffsetBpmnModelInstance, form.copyList());
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
    public List<String> createBriefDesc(InvoiceOffset entity) {
        return List.of();
    }

    @Override
    public InvoiceOffset getEntityWithCurrentTask(String entityId) {
        UserView user = TokenUtil.getUserFromAuthToken();
        InvoiceOffset load = this.load(entityId);
        setActiveTask(load);
        final ActRuTask task = load.getCurrentTask();
        if (task != null) {
//            final List<User> candidateUsers = this.getCandidateUsers(invoiceOffsetBpmnModelInstance, task.getTaskDefKey());
            final List<String> candidateUserStringList = this.getCandidateUserStringList(invoiceOffsetBpmnModelInstance, task.getTaskDefKey());
            if (candidateUserStringList.isEmpty()) {
                //没有候选人
                return load;
            }
            final List<User> list = this.userWrapper(candidateUserStringList);
            load.setCandidateUsers(list);
            final User appr = list.stream().filter(i -> user.getId().equals(i.getId())).findAny().orElse(null);
            load.setApproveUser(appr != null);
        }
        return load;
    }

    @Override
    public boolean isApproveUser(InvoiceOffset form) {
        log.info("invoiceOffsetService.isApproveUser");
        UserView user = TokenUtil.getUserFromAuthToken();
        String currentTaskId = form.getCurrentTaskId();
        final Task task = taskService.createTaskQuery().taskId(currentTaskId).active().singleResult();
        if (task != null) {
            if (StringUtils.isNotBlank(task.getAssignee())) {
                return task.getAssignee().equals(user.getId());
            }
            final List<User> candidateUsers = this.getCandidateUsers(invoiceOffsetBpmnModelInstance, task.getTaskDefinitionKey());
            final User appr = candidateUsers.stream().filter(i -> user.getId().equals(i.getId())).findAny().orElse(null);
            return appr != null;
        }
        return false;
    }

//    @Override
    public boolean supports(Task task) {
        final String procDefId = task.getProcessDefinitionId();
        if (StringUtils.isNotBlank(procDefId)) {
            return procDefId.contains(WorkFlowConfig.DEF_KEY_INVOFFSET);
        }
        return false;
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
