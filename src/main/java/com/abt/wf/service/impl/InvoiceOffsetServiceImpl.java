package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.entity.act.ActRuTask;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.UserTaskDTO;
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

    private final InvoiceOffsetRepository invoiceOffsetRepository;
    private final InvoiceOffsetTaskRepository invoiceOffsetTaskRepository;

    private final BpmnModelInstance invoiceOffsetBpmnModelInstance;

    public InvoiceOffsetServiceImpl(IdentityService identityService, FlowOperationLogService flowOperationLogService, TaskService taskService,
                                    @Qualifier("sqlServerUserService") UserService userService, RepositoryService repositoryService, RuntimeService runtimeService,
                                    InvoiceOffsetRepository invoiceOffsetRepository, InvoiceOffsetTaskRepository invoiceOffsetTaskRepository,
                                    @Qualifier("invoiceOffsetBpmnModelInstance") BpmnModelInstance invoiceOffsetBpmnModelInstance) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.invoiceOffsetRepository = invoiceOffsetRepository;
        this.invoiceOffsetTaskRepository = invoiceOffsetTaskRepository;
        this.invoiceOffsetBpmnModelInstance = invoiceOffsetBpmnModelInstance;
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
    public int countAllByCriteria(InvoiceOffsetRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<InvoiceOffset> findMyApplyByCriteriaPageable(InvoiceOffsetRequestForm requestForm) {
        return invoiceOffsetTaskRepository.findUserApplyList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getContractName());
    }

    @Override
    public int countMyApplyByCriteria(InvoiceOffsetRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<InvoiceOffset> findMyDoneByCriteriaPageable(InvoiceOffsetRequestForm requestForm) {
        return invoiceOffsetTaskRepository.findDoneList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getContractName());
    }

    @Override
    public int countMyDoneByCriteria(InvoiceOffsetRequestForm requestForm) {
        return invoiceOffsetTaskRepository.countDoneList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getContractName());
    }

    @Override
    public List<InvoiceOffset> findMyTodoByCriteria(InvoiceOffsetRequestForm requestForm) {
        return invoiceOffsetTaskRepository.findTodoList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getContractName());
    }

    @Override
    public int countMyTodoByCriteria(InvoiceOffsetRequestForm requestForm) {
        return invoiceOffsetTaskRepository.countTodoList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getState(), requestForm.getId(), requestForm.getContractName());
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
    public InvoiceOffset getEntityWithCurrentTask(String entityId) {
        UserView user = TokenUtil.getUserFromAuthToken();
        InvoiceOffset load = this.load(entityId);
        setActiveTask(load);
        final ActRuTask task = load.getCurrentTask();
        if (task != null) {
            final List<User> candidateUsers = this.getCandidateUsers(invoiceOffsetBpmnModelInstance, task.getTaskDefKey());
            load.setCandidateUsers(candidateUsers);
            final User appr = candidateUsers.stream().filter(i -> user.getId().equals(i.getId())).findAny().orElse(null);
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
        final List<User> candidateUsers = this.getCandidateUsers(invoiceOffsetBpmnModelInstance, task.getTaskDefinitionKey());
        final User appr = candidateUsers.stream().filter(i -> user.getId().equals(i.getId())).findAny().orElse(null);
        return appr != null;
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

//        public Specification<InvoiceOffset> leftJoinRuTask(InvoiceOffsetRequestForm form) {
//            return (root, query, builder) -> {
//                List<Predicate> predicates = new ArrayList<>();
//                Join<InvoiceOffset, ActRuTask> taskJoin = root.join("currentTask", JoinType.LEFT);
//                if (StringUtils.isNotBlank(form.getUserid())) {
//                    predicates.add(builder.equal(taskJoin.get("assignee"), form.getUserid()));
//                }
//                return builder.and(predicates.toArray(new Predicate[0]));
//            };
//        }

//        public Specification<InvoiceOffset> leftJoinHiTask(InvoiceOffsetRequestForm form) {
//            return (root, query, builder) -> {
//                List<Predicate> predicates = new ArrayList<>();
//                Join<InvoiceOffset, ActHiTaskInstance> taskJoin = root.join("invokedTask", JoinType.LEFT);
//                if (StringUtils.isNotBlank(form.getUserid())) {
//                    predicates.add(builder.equal(taskJoin.get("assignee"), form.getUserid()));
//                }
//                if (StringUtils.isNotBlank(form.getProcDefKey())) {
//                    predicates.add(builder.equal(taskJoin.get("procDefKey"), form.getTaskDefKey()));
//                }
//                predicates.add(builder.notLike(taskJoin.get("taskDefKey"), like("apply")));
//                if (form.getQueryMode() == 1) {
//                    //todo_
//                    predicates.add(builder.isNull(taskJoin.get("endTime")));
//                } else if (form.getQueryMode() == 2) {
//                    predicates.add(builder.isNotNull(taskJoin.get("endTime")));
//                }
//                return builder.and(predicates.toArray(new Predicate[0]));
//            };
//        }

    }
}
