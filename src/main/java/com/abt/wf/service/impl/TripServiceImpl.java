package com.abt.wf.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.entity.TripDetail;
import com.abt.wf.entity.TripMain;
import com.abt.wf.entity.TripOtherItem;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.TripRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.TripDetailRepository;
import com.abt.wf.repository.TripMainRepository;
import com.abt.wf.repository.TripOtherItemRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.TripService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service(WorkFlowConfig.DEF_KEY_TRIP)
public class TripServiceImpl extends AbstractWorkflowCommonServiceImpl<TripMain, TripRequestForm> implements TripService  {

    private final IdentityService identityService;
    private final UserService userService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final BpmnModelInstance rbsTripBpmnModelInstance;
    private final TripMainRepository tripMainRepository;
    private final TripDetailRepository tripDetailRepository;
    private final TripOtherItemRepository tripOtherItemRepository;

    @Value("${wf.trip.url.pre}")
    private String urlPrefix;

    public TripServiceImpl(IdentityService identityService, @Qualifier("sqlServerUserService") UserService userService, TaskService taskService,
                           FlowOperationLogService flowOperationLogService, RepositoryService repositoryService, RuntimeService runtimeService,
                           BpmnModelInstance rbsTripBpmnModelInstance, TripMainRepository tripMainRepository, TripDetailRepository tripDetailRepository, TripOtherItemRepository tripOtherItemRepository) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.identityService = identityService;
        this.userService = userService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.rbsTripBpmnModelInstance = rbsTripBpmnModelInstance;
        this.tripMainRepository = tripMainRepository;
        this.tripDetailRepository = tripDetailRepository;
        this.tripOtherItemRepository = tripOtherItemRepository;
    }


    @Override
    ValidationResult beforePreview(TripMain form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(TripMain form) {
        return form.getDecision();
    }

    @Override
    void passHandler(TripMain form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(TripMain form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(TripMain form) {

    }

    @Override
    void setApprovalResult(TripMain form, TripMain entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    void clearEntityId(TripMain entity) {
        entity.setId(null);
    }


    @Override
    public List<TripMain> findAllByCriteriaPageable(TripRequestForm requestForm) {
        return List.of();
    }

    @Override
    public int countAllByCriteria(TripRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<TripMain> findMyApplyByCriteriaPageable(TripRequestForm requestForm) {
        return List.of();
    }

    @Override
    public int countMyApplyByCriteria(TripRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<TripMain> findMyDoneByCriteriaPageable(TripRequestForm requestForm) {
        return List.of();
    }

    @Override
    public int countMyDoneByCriteria(TripRequestForm requestForm) {
        return 0;
    }

    @Override
    public List<TripMain> findMyTodoByCriteria(TripRequestForm requestForm) {
        return List.of();
    }

    @Override
    public int countMyTodoByCriteria(TripRequestForm requestForm) {
        return 0;
    }

    @Transactional
    @Override
    public TripMain saveEntity(TripMain form) {
        tripMainRepository.save(form);
        final String mid = form.getId();
        for (TripDetail d : form.getDetails()) {
            d.setMid(mid);
            List<TripOtherItem> iList = d.getItems();
            d = tripDetailRepository.save(d);
            String did = d.getId();
            iList.forEach(i -> {
                i.setDid(did);
                tripOtherItemRepository.save(i);
            });
        }
        return form;
    }

    @Override
    public TripMain load(String entityId) {
        final TripMain main = tripMainRepository.findWithCurrentTaskById(entityId);
        if (main == null) {
            throw new BusinessException("未查询到差旅报销申请(id=" + entityId + ")");
        }
        return main;
    }

    @Override
    public String getEntityId(TripMain entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return Constants.SERVICE_TRIP;
    }

    @Override
    public Page<TripMain> findAllByQueryPageable(TripRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"));
        final Page<TripMain> page = tripMainRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        if (!page.getContent().isEmpty()) {
            page.getContent().forEach(this::buildActiveTask);
        }
        return page;
    }

    @Override
    public Page<TripMain> findMyApplyByQueryPageable(TripRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"));
//        Pageable pageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.DESC, "createDate"));
        final Page<TripMain> page = tripMainRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        if (!page.getContent().isEmpty()) {
            page.getContent().forEach(this::buildActiveTask);
        }
        return page;
    }

    //审批编号/出差人员/总金额/申请人,状态,创建时间
    @Override
    public Page<TripMain> findMyTodoByQueryPageable(TripRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"));
        final Page<TripMain> page = tripMainRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        if (!page.getContent().isEmpty()) {
            page.getContent().forEach(this::buildActiveTask);
        }
        return page;
    }

    @Override
    public Page<TripMain> findMyDoneByQueryPageable(TripRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"));
        final Page<TripMain> page = tripMainRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        if (!page.getContent().isEmpty()) {
            page.getContent().forEach(this::buildActiveTask);
        }
        return page;
    }

    @Override
    public Map<String, Object> createVariableMap(TripMain form) {
        return form.createVariableMap();
    }

    @Override
    public String businessKey(TripMain form) {
        return Constants.SERVICE_TRIP;
    }

    @Override
    public List<UserTaskDTO> preview(TripMain form) {
        return this.commonPreview(form, form.createVariableMap(), rbsTripBpmnModelInstance, form.copyList());
    }

    @Override
    public void ensureEntityId(TripMain form) {
        ValidateUtil.ensurePropertyNotnull(form.getId(), "审批编号");
    }

    @Override
    public boolean isApproveUser(TripMain form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return urlPrefix + id;
    }

    @Override
    public List<String> createBriefDesc(TripMain entity) {
        return List.of();
    }

    @Override
    public TripMain getEntityWithCurrentTask(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        final TripMain main = this.load(id);
        buildActiveTask(main);
        main.setApproveUser(user.getId().equals(main.getCurrentTaskAssigneeId()));
        return main;
    }
}
