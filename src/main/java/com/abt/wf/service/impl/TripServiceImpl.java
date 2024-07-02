package com.abt.wf.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.common.util.ValidateUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
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
    public TripMain saveEntity(TripMain entity) {
        //设置关联
        List<TripOtherItem> list = new ArrayList<>();
        entity = tripMainRepository.save(entity);
        final String mid = entity.getId();
        entity.getDetails().forEach(d -> {
            d.setMid(mid);
            d = tripDetailRepository.save(d);
            final String did = d.getId();
            System.out.println("did==" + did);
            d.getItems().forEach(i -> {
                i.setDid(did);
                list.add(i);
            });
        });

        tripOtherItemRepository.saveAll(list);
        return entity;
    }

    @Override
    public TripMain load(String entityId) {
        return tripMainRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到差旅报销申请(id=" + entityId + ")"));
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
        return null;
    }

    @Override
    public Page<TripMain> findMyApplyByQueryPageable(TripRequestForm requestForm) {
        return null;
    }

    //审批编号/出差人员/总金额/申请人,状态,创建时间
    @Override
    public Page<TripMain> findMyTodoByQueryPageable(TripRequestForm requestForm) {
        return null;
    }

    @Override
    public Page<TripMain> findMyDoneByQueryPageable(TripRequestForm requestForm) {
        return null;
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
    public TripMain getEntityWithCurrentTask(String id) {

        return null;
    }
}
