package com.abt.wf.service.impl;

import cn.idev.excel.FastExcel;
import com.abt.common.model.RequestForm;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.model.TaskCheckUser;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import com.abt.wf.repository.SubcontractTestingRepository;
import com.abt.wf.repository.SubcontractTestingSampleRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.SignatureService;
import com.abt.wf.service.SubcontractTestingService;
import jakarta.servlet.http.HttpServletResponse;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_SBCT;

/**
 *
 */
@Service(DEF_KEY_SBCT)
public class SubcontractTestingServiceImpl extends AbstractWorkflowCommonServiceImpl<SubcontractTesting, SubcontractTestingRequestForm> implements SubcontractTestingService{

    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;

    private final BpmnModelInstance subcontractTestingBpmnModelInstance;

    private final IFileService fileService;

    private final HistoryService historyService;
    private final SignatureService signatureService;


    private final SubcontractTestingRepository subcontractTestingRepository;
    private final SubcontractTestingSampleRepository subcontractTestingSampleRepository;


    @Value("${abt.wf.sbct.rock.template}")
    private String rockSampleListTemplate;

    @Value("${abt.wf.sbct.fluid.template}")
    private String fluidSampleListTemplate;

    @Value("${abt.wf.sbct.export.dir}")
    private String sampleListExportDir;

    public SubcontractTestingServiceImpl(IdentityService identityService, RepositoryService repositoryService,
                                         RuntimeService runtimeService, TaskService taskService,
                                         FlowOperationLogService flowOperationLogService,
                                         @Qualifier("sqlServerUserService") UserService userService,
                                         @Qualifier("sbctBpmnModelInstance")BpmnModelInstance subcontractTestingBpmnModelInstance, IFileService fileService, HistoryService historyService,
                                         SignatureService signatureService,
                                         SubcontractTestingRepository subcontractTestingRepository, SubcontractTestingSampleRepository subcontractTestingSampleRepository) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService, signatureService);

        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.subcontractTestingBpmnModelInstance = subcontractTestingBpmnModelInstance;
        this.fileService = fileService;
        this.historyService = historyService;
        this.signatureService = signatureService;
        this.subcontractTestingRepository = subcontractTestingRepository;
        this.subcontractTestingSampleRepository = subcontractTestingSampleRepository;
    }


    @Transactional
    @Override
    public SubcontractTesting saveEntity(SubcontractTesting entity) {
        final SubcontractTesting saved = subcontractTestingRepository.save(entity);
        final List<SubcontractTestingSample> sampleList = entity.getSampleList();
        if (sampleList != null && sampleList.size() > 0) {
            sampleList.forEach(sample -> {
                sample.setMid(saved.getId());
            });
            final List<SubcontractTestingSample> list = subcontractTestingSampleRepository.saveAll(sampleList);
            saved.setSampleList(list);
        }
        return saved;
    }

    @Override
    public SubcontractTesting load(String entityId) {
        final SubcontractTesting entity = subcontractTestingRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到外送申请(id=" + entityId + ")"));
        setActiveTask(entity);
        return entity;
    }

    @Override
    public SubcontractTesting loadWithSampleList(String entityId) {
        final SubcontractTesting entity = subcontractTestingRepository.findWithSampleList(entityId).orElseThrow(() -> new BusinessException("未查询到外送申请(id=" + entityId + ")"));
        setActiveTask(entity);
        return entity;
    }

    @Override
    public String getEntityId(SubcontractTesting entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_SUBCONTRACT_TESTING;
    }

    @Override
    public Page<SubcontractTesting> findAllByQueryPageable(SubcontractTestingRequestForm requestForm) {
        Pageable pageable = requestForm.createDefaultPageable();
        final Page<SubcontractTesting> page = subcontractTestingRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<SubcontractTesting> findMyApplyByQueryPageable(SubcontractTestingRequestForm requestForm) {
        Pageable pageable = requestForm.createDefaultPageable();
        final Page<SubcontractTesting> page = subcontractTestingRepository.findUserApplyByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<SubcontractTesting> findMyTodoByQueryPageable(SubcontractTestingRequestForm requestForm) {
        Pageable pageable = requestForm.createDefaultPageable();
        final Page<SubcontractTesting> page = subcontractTestingRepository.findUserTodoByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<SubcontractTesting> findMyDoneByQueryPageable(SubcontractTestingRequestForm requestForm) {
        Pageable pageable = requestForm.createDefaultPageable();
        final Page<SubcontractTesting> page = subcontractTestingRepository.findUserDoneByQueryPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public int countMyTodo(SubcontractTestingRequestForm form) {
        return subcontractTestingRepository.countTodoByQuery(form.getUserid(), form.getQuery(), form.getTaskDefKey());
    }

    @Override
    public int countMyTodoByRequestForm(RequestForm requestForm) {
        return subcontractTestingRepository.countTodoByQuery(requestForm.getUserid(), requestForm.getQuery(), requestForm.getTaskDefKey());
    }

    @Override
    public List<SubcontractTesting> findMyTodoList(RequestForm requestForm) {
        final List<SubcontractTesting> list = subcontractTestingRepository.findUserTodoList(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), requestForm.getTaskDefKey());
        list.forEach(this::buildActiveTask);
        return list;
    }

    @Override
    public SubcontractTestingRequestForm createRequestForm() {
        return new SubcontractTestingRequestForm();
    }

    @Override
    ValidationResult beforePreview(SubcontractTesting form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(SubcontractTesting form) {
        return form.getDecision();
    }

    @Override
    void passHandler(SubcontractTesting form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(SubcontractTesting form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(SubcontractTesting form) {

    }

    @Override
    void setApprovalResult(SubcontractTesting form, SubcontractTesting entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
        runtimeService.setVariable(entity.getProcessInstanceId(), VAR_KEY_APPR_RESULT, form.getDecision());
    }

    @Override
    void setFileListJson(SubcontractTesting entity, String json) {

    }

    @Override
    String getAttachmentJson(SubcontractTesting form) {
        return "";
    }

    @Override
    void clearEntityId(SubcontractTesting entity) {
        entity.setId(null);
    }

    @Override
    public void export(SubcontractTestingRequestForm requestForm, HttpServletResponse response, String templatePath, String newFileName, Class<SubcontractTesting> dataClass) throws IOException {

    }

    @Override
    public Map<String, Object> createVariableMap(SubcontractTesting form) {
        final Map<String, Object> varMap = form.createVarMap();
        if (form.getCheckUsers() != null) {
            form.getCheckUsers().forEach(u -> {
                varMap.put(u.getTaskDefKey() + "_userid", null);
                if (u.getUsers() != null && !u.getUsers().isEmpty() && u.getUsers().get(0) != null) {
                    varMap.put(u.getTaskDefKey() + "_userid", u.getUsers().get(0).getId());
                }
            });
        }
        return varMap;
    }

    @Override
    public String businessKey(SubcontractTesting form) {
        return SERVICE_SUBCONTRACT_TESTING;
    }

    @Override
    public List<UserTaskDTO> preview(SubcontractTesting form) {
        final List<UserTaskDTO> tasks = this.commonPreview(form, form.createVarMap(), subcontractTestingBpmnModelInstance, List.of());
//        去掉申请
        return tasks.stream().filter(t -> !t.isApplyNode()).toList();
    }


    @Override
    public void ensureEntityId(SubcontractTesting form) {
        ensureProperty(form.getId(), "审批编号(id)");
    }

    @Override
    public boolean isApproveUser(SubcontractTesting form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return "";
    }

    @Override
    public List<String> createBriefDesc(SubcontractTesting entity) {
        return List.of();
    }

    @Override
    public String exportSampleList(String id) {
        final SubcontractTesting main = loadWithSampleList(id);
        List<SubcontractTestingSample> list = main.getSampleList();
        list.sort(Comparator.comparing(SubcontractTestingSample::getNewSampleNo));
        String template = "";
        if (SBCT_SAMPLE_TYPE_ROCK.equals(main.getSampleType())) {
            template = rockSampleListTemplate;
        } else if (SBCT_SAMPLE_TYPE_GAS.equals(main.getSampleType())) {
            template = fluidSampleListTemplate;
        }
        String fileName = sampleListExportDir + System.currentTimeMillis() + ".xlsx";
        FastExcel.write(fileName).withTemplate(template).sheet().doFill(list);
        return fileName;
    }

    @Override
    public List<String> findAllApplySubcontractCompany() {
        return subcontractTestingRepository.findAllSubcontractCompanies();
    }

    @Override
    public List<SubcontractTestingSettlementDetailProjection> findDetails(SubcontractTestingRequestForm requestForm) {
        return subcontractTestingSampleRepository.findDetailsBy(requestForm.getUserid(), requestForm.getSubcontractCompanyName());
    }

}
