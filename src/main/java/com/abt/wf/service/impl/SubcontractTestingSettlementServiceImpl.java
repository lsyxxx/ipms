package com.abt.wf.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.model.ValidationResult;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.model.SubcontractTestingSettlementRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.SubcontractTestingRepository;
import com.abt.wf.repository.SubcontractTestingSampleRepository;
import com.abt.wf.repository.SubcontractTestingSettlementMainRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.SignatureService;
import com.abt.wf.service.SubcontractTestingSettlementService;
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
import java.util.Comparator;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_SBCT_STL;

/**
 *
 */
@Service(DEF_KEY_SBCT_STL)
public class SubcontractTestingSettlementServiceImpl extends AbstractWorkflowCommonServiceImpl<SubcontractTestingSettlementMain, SubcontractTestingSettlementRequestForm> implements SubcontractTestingSettlementService {

    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;
    private final IFileService fileService;
    private final HistoryService historyService;
    private final SignatureService signatureService;

    private final SubcontractTestingRepository subcontractTestingRepository;
    private final SubcontractTestingSettlementMainRepository subcontractTestingSettlementMainRepository;
    private final SubcontractTestingSampleRepository subcontractTestingSampleRepository;
    private final BpmnModelInstance sbctStlBpmnModelInstance;

    public SubcontractTestingSettlementServiceImpl(IdentityService identityService, RepositoryService repositoryService,
                                                   RuntimeService runtimeService, TaskService taskService,
                                                   FlowOperationLogService flowOperationLogService,
                                                   @Qualifier("sqlServerUserService") UserService userService, IFileService fileService,
                                                   HistoryService historyService, SignatureService signatureService, SubcontractTestingRepository subcontractTestingRepository, SubcontractTestingSettlementMainRepository subcontractTestingSettlementMainRepository, SubcontractTestingSampleRepository subcontractTestingSampleRepository, BpmnModelInstance sbctStlBpmnModelInstance) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService,signatureService);
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.fileService = fileService;
        this.historyService = historyService;
        this.signatureService = signatureService;
        this.subcontractTestingRepository = subcontractTestingRepository;
        this.subcontractTestingSettlementMainRepository = subcontractTestingSettlementMainRepository;
        this.subcontractTestingSampleRepository = subcontractTestingSampleRepository;
        this.sbctStlBpmnModelInstance = sbctStlBpmnModelInstance;
    }


    @Override
    ValidationResult beforePreview(SubcontractTestingSettlementMain form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(SubcontractTestingSettlementMain form) {
        return form.getDecision();
    }

    @Override
    void passHandler(SubcontractTestingSettlementMain form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(SubcontractTestingSettlementMain form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(SubcontractTestingSettlementMain form) {

    }

    @Override
    void setApprovalResult(SubcontractTestingSettlementMain form, SubcontractTestingSettlementMain entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    void setFileListJson(SubcontractTestingSettlementMain entity, String json) {

    }

    @Override
    String getAttachmentJson(SubcontractTestingSettlementMain form) {
        return "";
    }

    @Override
    void clearEntityId(SubcontractTestingSettlementMain entity) {
        entity.setId(null);
    }

    @Override
    public SubcontractTestingSettlementMain saveEntity(SubcontractTestingSettlementMain entity) {
        if (entity.getDetails() != null) {
            entity.getDetails().forEach(d -> {
                d.setMain(entity);
            });
        }
        return subcontractTestingSettlementMainRepository.save(entity);
    }

    @Override
    public SubcontractTestingSettlementMain load(String entityId) {
        return subcontractTestingSettlementMainRepository.findById(entityId).orElseThrow(() -> new BusinessException(String.format("未查询到外送检测结算申请(id=%s)", entityId)));
    }

    @Override
    public String getEntityId(SubcontractTestingSettlementMain entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_SBCT_STL;
    }

    @Override
    public Page<SubcontractTestingSettlementMain> findAllByQueryPageable(SubcontractTestingSettlementRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<SubcontractTestingSettlementMain> paged = subcontractTestingSettlementMainRepository.findAllByQueryPaged(null,
                requestForm.getQuery(), requestForm.getState(),
                requestForm.toLocalStartTime(),
                requestForm.toLocalEndTime(),
                pageable);
        paged.getContent().forEach(this::buildActiveTask);
        return paged;
    }

    @Override
    public Page<SubcontractTestingSettlementMain> findMyApplyByQueryPageable(SubcontractTestingSettlementRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<SubcontractTestingSettlementMain> page = subcontractTestingSettlementMainRepository.findMyApplyPaged(requestForm.getUserid(), 
                requestForm.getQuery(), requestForm.getState(),
                requestForm.toLocalStartTime(),
                requestForm.toLocalEndTime(),
                pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<SubcontractTestingSettlementMain> findMyTodoByQueryPageable(SubcontractTestingSettlementRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<SubcontractTestingSettlementMain> page = subcontractTestingSettlementMainRepository.findMyTodoPaged(requestForm.getUserid(), 
                requestForm.getQuery(), requestForm.getState(),
                requestForm.toLocalStartTime(),
                requestForm.toLocalEndTime(),
                pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<SubcontractTestingSettlementMain> findMyDoneByQueryPageable(SubcontractTestingSettlementRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<SubcontractTestingSettlementMain> page = subcontractTestingSettlementMainRepository.findMyDonePaged(requestForm.getUserid(), 
                requestForm.getQuery(), requestForm.getState(),
                requestForm.toLocalStartTime(),
                requestForm.toLocalEndTime(),
                pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public int countMyTodo(SubcontractTestingSettlementRequestForm form) {
        return subcontractTestingSettlementMainRepository.countTodoByQuery(form.getUserid(), form.getQuery(), form.getTaskDefKey());
    }

    @Override
    public int countMyTodoByRequestForm(RequestForm requestForm) {
        return subcontractTestingSettlementMainRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public List<SubcontractTestingSettlementMain> findMyTodoList(RequestForm requestForm) {
        final List<SubcontractTestingSettlementMain> list = subcontractTestingSettlementMainRepository.findUserTodoList(
                requestForm.getUserid(), 
                requestForm.getQuery(), 
                requestForm.getState(),
                requestForm.toLocalStartTime(),
                requestForm.toLocalEndTime(),
                requestForm.getTaskDefKey());
        list.forEach(this::buildActiveTask);
        return list;
    }

    @Override
    public SubcontractTestingSettlementRequestForm createRequestForm() {
        return new SubcontractTestingSettlementRequestForm();
    }

    @Override
    public Map<String, Object> createVariableMap(SubcontractTestingSettlementMain form) {
        final Map<String, Object> varMap = form.createVarMap();
        if (form.getCheckUsers() != null) {
            form.getCheckUsers().forEach(u -> {
                //单人审批
                if (u.getUsers() != null && !u.getUsers().isEmpty() && u.getUsers().get(0) != null) {
                    varMap.put(u.getTaskDefKey() + "_userid", u.getUsers().get(0).getId());
                } else {
                    varMap.put(u.getTaskDefKey() + "_userid", null);
                }
            });
        }
        return varMap;
    }

    @Override
    public String businessKey(SubcontractTestingSettlementMain form) {
        return SERVICE_SBCT_STL;
    }

    @Override
    public List<UserTaskDTO> preview(SubcontractTestingSettlementMain form) {
        final List<UserTaskDTO> tasks = this.commonPreview(form, createVariableMap(form), sbctStlBpmnModelInstance, form.copyList());
        return tasks.stream().filter(t -> !t.isApplyNode()).toList();
    }

    @Override
    public void ensureEntityId(SubcontractTestingSettlementMain form) {
        ensureProperty(form.getId(), "外送检测结算-审批编号(id)");
    }

    @Override
    public boolean isApproveUser(SubcontractTestingSettlementMain form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return "/wf/sbctstl/load/" + id;
    }

    @Override
    public List<String> createBriefDesc(SubcontractTestingSettlementMain entity) {
        return List.of(
            "外送单位: " + entity.getSubcontractCompany(),
            "外送日期: " + entity.getSbctStartDate() + " ~ " + entity.getSbctEndDate(),
            "报告返回日期: " + entity.getReportStartDate() + " ~ " + entity.getReportEndDate()
        );
    }

    @Override
    public SubcontractTestingSettlementMain loadEntireEntity(String id) {
        final SubcontractTestingSettlementMain entity = subcontractTestingSettlementMainRepository.findWithDetails(id);
        if (entity == null) {
            throw new BusinessException("未查询到业务(id=" + id + ")");
        }
        if (entity.getDetails() != null) {
            entity.getDetails().sort(Comparator
                .comparing(SubcontractTestingSettlementDetail::getEntrustId, Comparator.nullsLast(String::compareTo))
                .thenComparing(SubcontractTestingSettlementDetail::getCheckModuleName, 
                              Comparator.nullsLast(String::compareTo)));
        }
        setActiveTask(entity);
        return entity;
    }
}
