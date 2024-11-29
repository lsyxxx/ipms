package com.abt.wf.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.repository.FlowSettingRepository;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PurchaseService;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_PURCHASE;

/**
 *
 */
@Service(DEF_KEY_PURCHASE)
@Slf4j
public class PurchaseServiceImpl extends AbstractWorkflowCommonServiceImpl<PurchaseApplyMain, PurchaseApplyRequestForm> implements PurchaseService {
    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;
    private final FlowSettingRepository flowSettingRepository;

    private final BpmnModelInstance purchaseBpmnModelInstance;

    private final IFileService fileService;
    private final HistoryService historyService;
    private final SignatureService signatureService;

    private final PurchaseApplyMainRepository purchaseApplyMainRepository;



    public PurchaseServiceImpl(IdentityService identityService, RepositoryService repositoryService, RuntimeService runtimeService,
                               TaskService taskService, FlowOperationLogService flowOperationLogService,
                               @Qualifier("sqlServerUserService") UserService userService, FlowSettingRepository flowSettingRepository,
                               BpmnModelInstance purchaseBpmnModelInstance,
                               IFileService fileService, HistoryService historyService, SignatureService signatureService,
                               PurchaseApplyMainRepository purchaseApplyMainRepository) {

        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService,signatureService);
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.flowSettingRepository = flowSettingRepository;
        this.purchaseBpmnModelInstance = purchaseBpmnModelInstance;
        this.fileService = fileService;
        this.historyService = historyService;
        this.signatureService = signatureService;
        this.purchaseApplyMainRepository = purchaseApplyMainRepository;
    }


    @Override
    ValidationResult beforePreview(PurchaseApplyMain form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(PurchaseApplyMain form) {
        return form.getDecision();
    }

    @Override
    void passHandler(PurchaseApplyMain form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(PurchaseApplyMain form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(PurchaseApplyMain form) {

    }

    @Override
    public List<FlowOperationLog> processRecord(String entityId, String serviceName) {
        return this.simpleProcessRecord(entityId, serviceName);
    }

    /**
     * 自动跳过审批人为空的节点
     * @param form 当前form
     */
    @Override
    public void skipEmptyUserTask(PurchaseApplyMain form) {
        final Task currentTask = taskService.createTaskQuery().processInstanceId(form.getProcessInstanceId()).active().singleResult();
        if (currentTask == null) {
            return;
        }
        final String assignee = currentTask.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            return;
        }
        FlowOperationLog optLog = FlowOperationLog.autoPassLog(form, currentTask, form.getId());
        flowOperationLogService.saveLog(optLog);
        taskService.complete(currentTask.getId());
        skipEmptyUserTask(form);
    }

    @Override
    void setApprovalResult(PurchaseApplyMain form, PurchaseApplyMain entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    void setFileListJson(PurchaseApplyMain entity, String json) {

    }

    @Override
    String getAttachmentJson(PurchaseApplyMain form) {
        return "";
    }

    @Override
    void clearEntityId(PurchaseApplyMain entity) {
        entity.setId(null);
    }

    @Override
    public PurchaseApplyMain saveEntity(PurchaseApplyMain entity) {
        List<PurchaseApplyDetail> details = entity.getDetails();
        if (details != null) {
            details.forEach(d -> {
                d.setMain(entity);
                //处理最终完成数量
                d.handleFinalQuantity();
            });
        }
        return purchaseApplyMainRepository.save(entity);
    }

    @Override
    public PurchaseApplyMain load(String entityId) {
        final PurchaseApplyMain main = purchaseApplyMainRepository.findByIdWithDetails(entityId);
        setActiveTask(main);
        return main;
    }

    @Override
    public String getEntityId(PurchaseApplyMain entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_PURCHASE;
    }

    @Override
    public Page<PurchaseApplyMain> findAllByQueryPageable(PurchaseApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PurchaseApplyMain> paged = purchaseApplyMainRepository.findAllByQueryPaged(requestForm.getUserid(),
                requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                pageable);
        paged.getContent().forEach(this::buildActiveTask);
        return paged;
    }

    @Override
    public Page<PurchaseApplyMain> findMyApplyByQueryPageable(PurchaseApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PurchaseApplyMain> myApplyPaged = purchaseApplyMainRepository.findMyApplyPaged(requestForm.getUserid(),
                requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                pageable);
        myApplyPaged.getContent().forEach(this::buildActiveTask);
        return myApplyPaged;
    }

    @Override
    public Page<PurchaseApplyMain> findMyTodoByQueryPageable(PurchaseApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PurchaseApplyMain> paged = purchaseApplyMainRepository.findMyTodoPaged(requestForm.getUserid(),
                requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                pageable);
        paged.getContent().forEach(this::buildActiveTask);
        return paged;
    }

    @Override
    public Page<PurchaseApplyMain> findMyDoneByQueryPageable(PurchaseApplyRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<PurchaseApplyMain> paged = purchaseApplyMainRepository.findMyDonePaged(requestForm.getUserid(),
                requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()),
                TimeUtil.toLocalDateTime(requestForm.getEndDate()),
                pageable);
        paged.getContent().forEach(this::buildActiveTask);
        return paged;
    }

    @Override
    public int countMyTodo(PurchaseApplyRequestForm requestForm) {
        return purchaseApplyMainRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public int countMyTodoByRequestForm(RequestForm requestForm) {
        return purchaseApplyMainRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public List<PurchaseApplyMain> findMyTodoList(RequestForm requestForm) {
        return purchaseApplyMainRepository.findUserTodoList(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), requestForm.getTaskDefKey());
    }

    @Override
    public PurchaseApplyRequestForm createRequestForm() {
        return new PurchaseApplyRequestForm();
    }

    @Override
    public Map<String, Object> createVariableMap(PurchaseApplyMain form) {
        return form.getVariableMap();
    }

    @Override
    public String businessKey(PurchaseApplyMain form) {
        return SERVICE_PURCHASE;
    }

    @Override
    public List<UserTaskDTO> preview(PurchaseApplyMain form) {
        return this.commonPreview(form, createVariableMap(form), purchaseBpmnModelInstance, form.copyList());
    }

    @Override
    public void ensureEntityId(PurchaseApplyMain form) {
        ensureProperty(form.getId(), "采购申请-审批编号(id)");
    }

    @Override
    public boolean isApproveUser(PurchaseApplyMain form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return "";
    }

    @Override
    public List<String> createBriefDesc(PurchaseApplyMain entity) {
        return List.of();
    }

    @Override
    public void tempSave(PurchaseApplyMain entity) {
        entity.setBusinessState(STATE_DETAIL_TEMP);
        if (SAVE_TYPE_NEW.equals(entity.getSaveType())) {
            entity.setId(null);
        }
        this.saveEntity(entity);
    }

    @Override
    public PurchaseApplyMain getCopyEntity(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("请选择一个流程提交");
        }

        PurchaseApplyMain entity = purchaseApplyMainRepository.findByIdWithDetails(id);
        clearEntityId(entity);
        entity.getDetails().forEach(d -> {
            d.setId(null);
            d.setMain(entity);
        });
        return entity;
    }

    @Override
    public void setCostVariable(PurchaseApplyMain entity) {
        runtimeService.setVariable(entity.getProcessInstanceId(), PurchaseApplyMain.KEY_COST, entity.getCost());
    }


    public void todoCount(String userid) {

    }


}
