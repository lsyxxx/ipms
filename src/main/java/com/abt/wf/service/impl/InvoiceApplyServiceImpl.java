package com.abt.wf.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.*;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.InvoiceApplyService;
import com.abt.wf.service.SignatureService;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.common.util.QueryUtil.like;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_INV;

/**
 * 开票申请
 */
@Service(DEF_KEY_INV)
public class InvoiceApplyServiceImpl extends AbstractWorkflowCommonServiceImpl<InvoiceApply, InvoiceApplyRequestForm> implements InvoiceApplyService {
    private final IdentityService identityService;
    private final UserService userService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final InvoiceApplyRepository invoiceApplyRepository;
    private final BpmnModelInstance invoiceApplyBpmnModelInstance;

    private final IFileService fileService;
    private final HistoryService historyService;
    private final SignatureService signatureService;


    @Value("${wf.inv.url.pre}")
    private String urlPrefix;

    public InvoiceApplyServiceImpl(IdentityService identityService, @Qualifier("sqlServerUserService")  UserService userService,
                                   TaskService taskService, FlowOperationLogService flowOperationLogService, RepositoryService repositoryService,
                                   RuntimeService runtimeService,
                                   InvoiceApplyRepository invoiceApplyRepository,
                                   BpmnModelInstance invoiceApplyBpmnModelInstance, IFileService fileService, HistoryService historyService, SignatureService signatureService) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService, signatureService);
        this.identityService = identityService;
        this.userService = userService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.invoiceApplyRepository = invoiceApplyRepository;
        this.invoiceApplyBpmnModelInstance = invoiceApplyBpmnModelInstance;
        this.fileService = fileService;
        this.historyService = historyService;
        this.signatureService = signatureService;
    }


    @Override
    ValidationResult beforePreview(InvoiceApply form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(InvoiceApply form) {
        return form.getDecision();
    }

    @Override
    void passHandler(InvoiceApply form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(InvoiceApply form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(InvoiceApply form) {
    }

    @Override
    void setApprovalResult(InvoiceApply form, InvoiceApply entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    void setFileListJson(InvoiceApply entity, String json) {
        entity.setFileList(json);
    }

    @Override
    String getAttachmentJson(InvoiceApply form) {
        return form.getFileList();
    }

    @Override
    void clearEntityId(InvoiceApply entity) {
        entity.setId(null);
    }

    static class InvoiceApplySpecifications extends CommonSpecifications<InvoiceApplyRequestForm, InvoiceApply> {
        //criteria 合同名称，合同编号, 客户id， 客户name, 项目名称, 申请部门id, 申请部门name
        public Specification<InvoiceApply> clientIdEqual(InvoiceApplyRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getClientId())) {
                    return builder.equal(root.get("clientId"), form.getClientId());
                }
                return null;
            };
        }

        public Specification<InvoiceApply> clientNameLike(InvoiceApplyRequestForm form) {
            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isNotBlank(form.getClientName())) {
                    return criteriaBuilder.equal(root.get("clientName"), like(form.getClientName()));
                }
                return null;
            };
        }

        public Specification<InvoiceApply> contractNoLike(InvoiceApplyRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getContractNo())) {
                    return builder.like(root.get("contractNo"), like(form.getContractNo()));
                }
                return null;
            };
        }

        public Specification<InvoiceApply> contractNameLike(InvoiceApplyRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getContractName())) {
                    return builder.like(root.get("contractName"), like(form.getContractName()));
                }
                return null;
            };
        }

        public Specification<InvoiceApply> projectLike(InvoiceApplyRequestForm form) {
            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isNotBlank(form.getProject())) {
                    return criteriaBuilder.equal(root.get("project"), like(form.getProject()));
                }
                return null;
            };
        }


        public Specification<InvoiceApply> deptIdEqual(InvoiceApplyRequestForm form) {
            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isNotBlank(form.getDeptId())) {
                    return criteriaBuilder.equal(root.get("deptId"), form.getDeptId());
                }
                return null;
            };
        }

        public Specification<InvoiceApply> deptNameLike(InvoiceApplyRequestForm form) {
            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isNotBlank(form.getDeptName())) {
                    return criteriaBuilder.equal(root.get("deptName"), like(form.getDeptName()));
                }
                return null;
            };
        }
    }

    @Override
    public Page<InvoiceApply> findMyApplyByQueryPageable(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceApply> findAllByQueryPageable(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceApply> findMyTodoByQueryPageable(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceApply> findMyDoneByQueryPageable(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public int countMyTodo(InvoiceApplyRequestForm requestForm) {
        return invoiceApplyRepository.countMyTodo(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public int countMyTodoByRequestForm(RequestForm requestForm) {
        return invoiceApplyRepository.countMyTodo(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public List<InvoiceApply> findMyTodoList(RequestForm requestForm) {
        final List<InvoiceApply> list = invoiceApplyRepository.findUserTodoList(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), requestForm.getTaskDefKey());
        list.forEach(this::buildActiveTask);
        return list;
    }

    @Override
    public InvoiceApplyRequestForm createRequestForm() {
        return new InvoiceApplyRequestForm();
    }

    @Override
    public InvoiceApply saveEntity(InvoiceApply entity) {
        return invoiceApplyRepository.save(entity);
    }

    @Override
    public InvoiceApply load(String entityId) {
        return invoiceApplyRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到开票申请流程(id=" + entityId + ")"));
    }

    @Override
    public InvoiceApply getEntityWithCurrentTask(String entityId) {
        InvoiceApply entity = this.load(entityId);
        setActiveTask(entity);
        return entity;
    }

    @Override
    public String getEntityId(InvoiceApply entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return Constants.SERVICE_INV;
    }

    @Override
    public Map<String, Object> createVariableMap(InvoiceApply form) {
        return form.createVariableMap();
    }

    @Override
    public String businessKey(InvoiceApply form) {
        return Constants.SERVICE_INV;
    }

    @Override
    public List<UserTaskDTO> preview(InvoiceApply form) {
        return this.commonPreview(form, form.createVariableMap(), invoiceApplyBpmnModelInstance, form.copyList());
    }

    @Override
    public void ensureEntityId(InvoiceApply form) {
        ensureProperty(form.getId(), "开票申请审批编号(id)");
    }

    @Override
    public boolean isApproveUser(InvoiceApply form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return urlPrefix + id ;
    }

    @Override
    public List<String> createBriefDesc(InvoiceApply entity) {
        return List.of();
    }

}
