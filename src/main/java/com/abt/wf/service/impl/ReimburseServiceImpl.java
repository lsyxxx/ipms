package com.abt.wf.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.repository.ReimburseTaskRepository;
import com.abt.wf.service.CommonSpecifications;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.ReimburseService;
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
import static com.abt.wf.config.Constants.SERVICE_RBS;

/**
 *
 */
@Service(WorkFlowConfig.DEF_KEY_RBS)
@Slf4j
public class ReimburseServiceImpl extends AbstractWorkflowCommonServiceImpl<Reimburse, ReimburseRequestForm> implements ReimburseService {

    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;

    private final ReimburseRepository reimburseRepository;
    private final ReimburseTaskRepository reimburseTaskRepository;
    private final BpmnModelInstance rbsBpmnModelInstance;

    public ReimburseServiceImpl(IdentityService identityService, RepositoryService repositoryService, RuntimeService runtimeService, TaskService taskService,
                                FlowOperationLogService flowOperationLogService, @Qualifier("sqlServerUserService") UserService userService, ReimburseRepository reimburseRepository,
                                ReimburseTaskRepository reimburseTaskRepository, @Qualifier("rbsBpmnModelInstance") BpmnModelInstance rbsBpmnModelInstance) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.reimburseRepository = reimburseRepository;
        this.reimburseTaskRepository = reimburseTaskRepository;
        this.rbsBpmnModelInstance = rbsBpmnModelInstance;
    }


    @Override
    ValidationResult beforePreview(Reimburse form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(Reimburse form) {
        return form.getDecision();
    }

    @Override
    void passHandler(Reimburse form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(Reimburse form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(Reimburse form) {

    }

    @Override
    @Deprecated
    public List<Reimburse> findAllByCriteriaPageable(ReimburseRequestForm requestForm) {
        return List.of();
    }

    @Override
    public Page<Reimburse> findAllByCriteria(ReimburseRequestForm requestForm) {
        requestForm.forcePaged();
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        ReimburseSpecification spec = new ReimburseSpecification();
        Specification<Reimburse> criteria = Specification.where(spec.beforeEndDate(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.projectNameLike(requestForm))
                .and(spec.createUsernameLike(requestForm))
                .and(spec.stateEqual(requestForm))
                .and(spec.entityIdLike(requestForm))
                .and(spec.createUseridEqual(requestForm));
        final Page<Reimburse> all = reimburseRepository.findAll(criteria, pageable);
        all.getContent().forEach(this::buildActiveTask);
        return all;
    }

    @Override
    @Deprecated
    public int countAllByCriteria(ReimburseRequestForm requestForm) {
        return 0;
    }

    @Override
    @Deprecated
    public List<Reimburse> findMyApplyByCriteriaPageable(ReimburseRequestForm requestForm) {
        return List.of();
    }

    @Override
    @Deprecated
    public int countMyApplyByCriteria(ReimburseRequestForm requestForm) {
        return 0;
    }

    @Override
    public Page<Reimburse> findMyApplyByCriteria(ReimburseRequestForm requestForm) {
        return this.findAllByCriteria(requestForm);
    }

    @Override
    public List<Reimburse> findMyDoneByCriteriaPageable(ReimburseRequestForm requestForm) {
        return reimburseTaskRepository.findDoneList(requestForm.getPage(),  requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getState(), requestForm.getProject());
    }

    @Override
    public int countMyDoneByCriteria(ReimburseRequestForm requestForm) {
        return reimburseTaskRepository.countDoneList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getState(), requestForm.getProject());
    }

    @Override
    public List<Reimburse> findMyTodoByCriteria(ReimburseRequestForm requestForm) {
        return reimburseTaskRepository.findTodoList(requestForm.getPage(), requestForm.getLimit(), requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getState(), requestForm.getProject());
    }

    @Override
    public int countMyTodoByCriteria(ReimburseRequestForm requestForm) {
        return reimburseTaskRepository.countTodoList(requestForm.getUserid(), requestForm.getUsername(),
                requestForm.getStartDate(), requestForm.getEndDate(), requestForm.getId(), requestForm.getState(), requestForm.getProject());
    }

    @Override
    public Reimburse saveEntity(Reimburse entity) {
        return reimburseRepository.save(entity);
    }

    @Override
    public Reimburse load(String entityId) {
         Reimburse reimburse = reimburseRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到费用报销单(审批编号=" + entityId + ")"));
         setActiveTask(reimburse);
         return reimburse;
    }

    @Override
    public String getEntityId(Reimburse entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_RBS;
    }

    @Override
    public Map<String, Object> createVariableMap(Reimburse form) {
        return form.createVariableMap();
    }

    @Override
    public String businessKey(Reimburse form) {
        return SERVICE_RBS;
    }

    @Override
    public List<UserTaskDTO> preview(Reimburse form) {
        return this.commonPreview(form, createVariableMap(form), rbsBpmnModelInstance, form.copyList());
    }

    @Override
    public void ensureEntityId(Reimburse form) {
        ensureProperty(form.getId(), "审批编号(id)");
    }

    @Override
    public String notifyLink(String id) {
        return "/wf/rbs/detail/" + id ;
    }

    static class ReimburseSpecification extends CommonSpecifications<ReimburseRequestForm, Reimburse> {
        public Specification<Reimburse> projectNameLike(ReimburseRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getProject())) {
                    return builder.like(root.get("project"), like(form.getProject()));
                }
                return null;
            };
        }
    }
}
