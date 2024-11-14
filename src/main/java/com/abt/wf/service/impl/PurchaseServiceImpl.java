package com.abt.wf.service.impl;

import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.repository.FlowSettingRepository;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.ActionEnum;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PurchaseService;
import com.abt.wf.service.SignatureService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;

/**
 *
 */
@Service
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

    @Transactional
    @Override
    public void apply(PurchaseApplyMain form) {
        //重写apply，申请节点不再放入流程图中
        WorkFlowUtil.ensureProcessDefinitionKey(form);
        final Map<String, Object> variableMap = this.createVariableMap(form);
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(form.getProcessDefinitionKey()).latestVersion().active().singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(form.getProcessDefinitionKey(), businessKey(form), variableMap);
        form.setProcessDefinitionId(processDefinition.getId());
        form.setProcessInstanceId(processInstance.getId());
        form.setProcessState(HistoricProcessInstance.STATE_ACTIVE);
        form.setBusinessState(STATE_DETAIL_ACTIVE);
        form.setServiceName(SERVICE_PURCHASE);
        final PurchaseApplyMain entity = this.saveEntity(form);
        final String id = this.getEntityId(entity);
        runtimeService.setVariable(form.getProcessInstanceId(), Constants.VAR_KEY_ENTITY, id);
        //log
        FlowOperationLog optLog = FlowOperationLog.create(form.getCreateUserid(), form.getCreateUsername(), form);
        optLog.setEntityId(id);
        optLog.setTaskName(ActionEnum.APPLY.name());
        optLog.setTaskStartTime(LocalDateTime.now());
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.APPLY.name());
        optLog.setTaskResult(STATE_DETAIL_APPLY);
        flowOperationLogService.saveLog(optLog);
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
        return null;
    }

    @Override
    public Page<PurchaseApplyMain> findMyApplyByQueryPageable(PurchaseApplyRequestForm requestForm) {
        return null;
    }

    @Override
    public Page<PurchaseApplyMain> findMyTodoByQueryPageable(PurchaseApplyRequestForm requestForm) {
        return null;
    }

    @Override
    public Page<PurchaseApplyMain> findMyDoneByQueryPageable(PurchaseApplyRequestForm requestForm) {
        return null;
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
}
