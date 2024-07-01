package com.abt.wf.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.*;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.abt.wf.repository.InvoiceApplyTaskRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.InvoiceApplyService;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
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
    private final InvoiceApplyTaskRepository invoiceApplyTaskRepository;
    private final InvoiceApplyRepository invoiceApplyRepository;
    private final BpmnModelInstance invoiceApplyBpmnModelInstance;

    public InvoiceApplyServiceImpl(IdentityService identityService, @Qualifier("sqlServerUserService")  UserService userService,
                                   TaskService taskService, FlowOperationLogService flowOperationLogService, RepositoryService repositoryService,
                                   RuntimeService runtimeService, InvoiceApplyTaskRepository invoiceApplyTaskRepository,
                                   InvoiceApplyRepository invoiceApplyRepository,
                                   BpmnModelInstance invoiceApplyBpmnModelInstance) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService;
        this.userService = userService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.invoiceApplyTaskRepository = invoiceApplyTaskRepository;
        this.invoiceApplyRepository = invoiceApplyRepository;
        this.invoiceApplyBpmnModelInstance = invoiceApplyBpmnModelInstance;
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
    public List<InvoiceApply> findAllByCriteriaPageable(InvoiceApplyRequestForm requestForm) {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称，合同编号, 客户id， 客户name, 项目名称，申请部门id, 申请部门name
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        InvoiceApplySpecifications specifications = new InvoiceApplySpecifications();
        Specification<InvoiceApply> criteria = Specification.where(specifications.createUseridEqual(requestForm))
                .and(specifications.beforeEndDate(requestForm))
                .and(specifications.afterStartDate(requestForm))
                .and(specifications.stateEqual(requestForm))
                .and(specifications.entityIdLike(requestForm))
                .and(specifications.contractNoLike(requestForm))
                .and(specifications.contractNameLike(requestForm))
                .and(specifications.clientIdEqual(requestForm))
                .and(specifications.clientNameLike(requestForm))
                .and(specifications.projectLike(requestForm))
                .and(specifications.deptIdEqual(requestForm))
                .and(specifications.deptNameLike(requestForm));
        return invoiceApplyRepository.findAll(criteria, pageable).getContent();
    }

    @Override
    public int countAllByCriteria(InvoiceApplyRequestForm requestForm) {
        InvoiceApplySpecifications specifications = new InvoiceApplySpecifications();
        Specification<InvoiceApply> criteria = Specification.where(specifications.createUseridEqual(requestForm))
                .and(specifications.beforeEndDate(requestForm))
                .and(specifications.afterStartDate(requestForm))
                .and(specifications.stateEqual(requestForm))
                .and(specifications.entityIdLike(requestForm))
                .and(specifications.contractNoLike(requestForm))
                .and(specifications.contractNameLike(requestForm))
                .and(specifications.clientIdEqual(requestForm))
                .and(specifications.clientNameLike(requestForm))
                .and(specifications.projectLike(requestForm))
                .and(specifications.deptIdEqual(requestForm))
                .and(specifications.deptNameLike(requestForm));
        return (int) invoiceApplyRepository.count(criteria);
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
    public List<InvoiceApply> findMyApplyByCriteriaPageable(InvoiceApplyRequestForm requestForm) {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称，合同编号, 客户id， 客户name, 项目名称，申请部门id, 申请部门name
        return invoiceApplyTaskRepository.findApplyListPageable(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getState(), requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getContractNo(), requestForm.getContractName(),
                requestForm.getClientId(), requestForm.getClientName(), requestForm.getProject(), requestForm.getDeptId(), requestForm.getDeptName());
    }

    @Override
    public Page<InvoiceApply> findMyApplyByQueryPaged(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceApply> findAllByQueryPaged(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceApply> findMyTodoByQueryPaged(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<InvoiceApply> findMyDoneByQueryPaged(InvoiceApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }



    @Override
    public int countMyApplyByCriteria(InvoiceApplyRequestForm requestForm) {
        //requestForm传入创建人
        return invoiceApplyTaskRepository.countApplyList( requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getState(), requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getContractNo(), requestForm.getContractName(),
                requestForm.getClientId(), requestForm.getClientName(), requestForm.getProject(), requestForm.getDeptId(), requestForm.getDeptName());
    }


    @Override
    public List<InvoiceApply> findMyDoneByCriteriaPageable(InvoiceApplyRequestForm requestForm) {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称，合同编号, 客户id， 客户name, 项目名称，申请部门id, 申请部门name
        return invoiceApplyTaskRepository.findDoneListPageable(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getState(), requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getContractNo(), requestForm.getContractName(),
                requestForm.getClientId(), requestForm.getClientName(), requestForm.getProject(), requestForm.getDeptId(), requestForm.getDeptName());
    }

    @Override
    public int countMyDoneByCriteria(InvoiceApplyRequestForm requestForm) {
        return invoiceApplyTaskRepository.countDoneList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getState(), requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getContractNo(), requestForm.getContractName(),
                requestForm.getClientId(), requestForm.getClientName(), requestForm.getProject(), requestForm.getDeptId(), requestForm.getDeptName());
    }

    @Override
    public List<InvoiceApply> findMyTodoByCriteria(InvoiceApplyRequestForm requestForm) {
        return invoiceApplyTaskRepository.findTodoListPageable(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getState(), requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getContractNo(), requestForm.getContractName(),
                requestForm.getClientId(), requestForm.getClientName(), requestForm.getProject(), requestForm.getDeptId(), requestForm.getDeptName());
    }

    @Override
    public int countMyTodoByCriteria(InvoiceApplyRequestForm requestForm) {
        return invoiceApplyTaskRepository.countTodoList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getState(), requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getContractNo(), requestForm.getContractName(),
                requestForm.getClientId(), requestForm.getClientName(), requestForm.getProject(), requestForm.getDeptId(), requestForm.getDeptName());
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
        return "/wf/inv/detail/" + id ;
    }

}
