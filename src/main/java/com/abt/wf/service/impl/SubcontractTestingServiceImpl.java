package com.abt.wf.service.impl;

import cn.idev.excel.FastExcel;
import com.abt.common.model.RequestForm;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.MoneyUtil;
import com.abt.common.util.TimeUtil;
import com.abt.market.entity.StlmTestTemp;
import com.abt.market.model.ImportSample;
import com.abt.market.repository.StlmTestTempRepository;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.SystemFile;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.repository.SampleRegistCheckModeuleItemRepository;
import com.abt.testing.repository.SampleRegistRepository;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import com.abt.wf.repository.SubcontractTestingRepository;
import com.abt.wf.repository.SubcontractTestingSampleRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.SignatureService;
import com.abt.wf.service.SubcontractTestingService;
import com.aspose.cells.*;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_SBCT;

/**
 *
 */
@Service(DEF_KEY_SBCT)
public class SubcontractTestingServiceImpl extends AbstractWorkflowCommonServiceImpl<SubcontractTesting, SubcontractTestingRequestForm> implements SubcontractTestingService {

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
    private final EntrustRepository entrustRepository;
    private final SampleRegistRepository sampleRegistRepository;
    private final SampleRegistCheckModeuleItemRepository sampleRegistCheckModeuleItemRepository;
    private final StlmTestTempRepository stlmTestTempRepository;

    @Value("${abt.wf.sbct.rock.template}")
    private String rockSampleListTemplate;

    @Value("${abt.wf.sbct.fluid.template}")
    private String fluidSampleListTemplate;

    @Value("${abt.wf.sbct.export.dir}")
    private String sampleListExportDir;

    @Value("${abt.wf.sbct.form.template}")
    private String formTemplate;

    public SubcontractTestingServiceImpl(IdentityService identityService, RepositoryService repositoryService,
                                         RuntimeService runtimeService, TaskService taskService,
                                         FlowOperationLogService flowOperationLogService,
                                         @Qualifier("sqlServerUserService") UserService userService,
                                         @Qualifier("sbctBpmnModelInstance") BpmnModelInstance subcontractTestingBpmnModelInstance, IFileService fileService, HistoryService historyService,
                                         SignatureService signatureService,
                                         SubcontractTestingRepository subcontractTestingRepository, SubcontractTestingSampleRepository subcontractTestingSampleRepository, EntrustRepository entrustRepository, SampleRegistRepository sampleRegistRepository, SampleRegistCheckModeuleItemRepository sampleRegistCheckModeuleItemRepository, StlmTestTempRepository stlmTestTempRepository) {
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
        this.entrustRepository = entrustRepository;
        this.sampleRegistRepository = sampleRegistRepository;
        this.sampleRegistCheckModeuleItemRepository = sampleRegistCheckModeuleItemRepository;
        this.stlmTestTempRepository = stlmTestTempRepository;
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
    public void validateEntity(String id) {
        final boolean exists = subcontractTestingRepository.existsById(id);
        if (!exists) {
            throw new BusinessException(String.format("外送申请单(%s)不存在", id));
        }


    }

    @Override
    public SubcontractTesting loadEntityOnly(String entityId) {
        return subcontractTestingRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到外送申请(id=" + entityId + ")"));

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
    public List<SubcontractTestingSample> getSamples(String entityId) {
        return subcontractTestingSampleRepository.findByMid(entityId);
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


    public void compareDuplicateSamples(List<SubcontractTestingSample> samples, List<SubcontractTestingSample> duplicatedSamples) {
        if (CollectionUtils.isEmpty(duplicatedSamples)) {
            return;
        }
        final Map<String, List<SubcontractTestingSample>> dupMap = duplicatedSamples.stream().collect(Collectors.groupingBy(this::getKey, Collectors.toList()));
        for (SubcontractTestingSample sample : samples) {
            String key = getKey(sample);
            if (dupMap.containsKey(key)) {
                dupMap.get(key).forEach(sample::addDuplicatedApplySample);
            }
        }
    }

    @Override
    public List<SubcontractTestingSample> validateDuplicatedSample(SubcontractTesting entity) {
        final List<SubcontractTestingSample> sampleList = entity.getSampleList();
        Set<String> sampleKeySet = new HashSet<>();
        if (sampleList != null) {
            //1. sampleList中有重复的
            sampleList.forEach(sample -> {
                String key = sample.getNewSampleNo() + sample.getCheckModuleId();
                if (!sampleKeySet.contains(key)) {
                    sampleKeySet.add(key);
                } else {
                    throw new BusinessException(String.format("存在重复外送样品: %s(%s)", sample.getNewSampleNo(), sample.getCheckModuleName()));
                }
            });
            //2. 和库里存在重复的
            List<SubcontractTestingSample> duplicatedSamples = new ArrayList<>();
            if (StringUtils.isEmpty(entity.getId())) {
                // 可能是未提交的申请
                duplicatedSamples = subcontractTestingSampleRepository.findSamplesByKey(sampleKeySet);
            } else {
                duplicatedSamples = findDuplicatedSamples(List.of(entity.getId()));
            }
            if (!CollectionUtils.isEmpty(duplicatedSamples)) {
                return duplicatedSamples;
            }
        } else {
            throw new BusinessException("请选择外送样品及检测项目!");
        }
        return List.of();
    }


    private String getKey(SubcontractTestingSample sample) {
        return sample.getNewSampleNo() + sample.getCheckModuleId();
    }

    @Override
    public Page<SubcontractTestingSample> findSamplesAndMarkDuplicated(List<String> ids, Pageable pageable) {
        //1. 查询ids的所有样品samples
        final Page<SubcontractTestingSample> samples = subcontractTestingSampleRepository.findByMids(ids, pageable);
        //2 查询库中重复样品
        final List<SubcontractTestingSample> duplicatedSamples = findDuplicatedSamples(ids);
        compareDuplicateSamples(samples.getContent(), duplicatedSamples);

        //3. 是否结算（TODO）
        return samples;
    }

    @Override
    public List<SubcontractTestingSample> findDuplicatedSamples(List<String> ids) {
        return subcontractTestingSampleRepository.findDuplicatedSamples(ids);
    }

    @Override
    public List<SbctSummaryData> getSummaryData(List<String> ids) {
        return subcontractTestingSampleRepository.getSummaryData(ids);
    }


    @Override
    public void createFormExcel(String id, OutputStream outputStream) throws Exception {
        // 从template中读取工作簿
        Workbook workbook = new Workbook(formTemplate);
        // 数据
        SubcontractTesting entity = loadWithSampleList(id);
        Worksheet worksheet = workbook.getWorksheets().get(0);
        createFormSheet(worksheet, entity);
        // //汇总数据
        // List<SbctSummaryData> summaryData = getSummaryData(List.of(entity.getId()));
        // createSummarySheet(worksheet, cells, summaryData);
        //样品清单
        Worksheet sampleSheet = workbook.getWorksheets().get("样品清单");
        createSampleSheet(sampleSheet, entity.getSampleList());

        // 保存到输出流
        workbook.save(outputStream, SaveFormat.XLSX);
    }

    private void createFormSheet(Worksheet worksheet, SubcontractTesting entity) {
        Cells cells = worksheet.getCells();
        // 第2行：申请编号和申请日期
        cells.get("B2").putValue(entity.getId());
        cells.get("E2").putValue(TimeUtil.toYYYY_MM_DDString(entity.getCreateDate()));

        // 第3行：经办人、部门、班组
        cells.get("B3").putValue(entity.getCreateUsername());
        cells.get("D3").putValue(entity.getCreateDeptName());
        cells.get("F3").putValue(entity.getCreateTeamName());

        // 第4行：样品种类、样品数量
        cells.get("B4").putValue(entity.getSampleType());
        cells.get("D4").putValue(entity.getSampleNum());

        // 第5行：外送原因
        cells.get("B5").putValue(entity.getReason());

        // 第6行：外送费用
        if (entity.getCost() != null) {
            cells.get("B6").putValue(entity.getCost());
        }
        //外送金额大写
        cells.get("D6").putValue(MoneyUtil.toUpperCase(entity.getCost().toString()));
        // 第7行：委托单位
        cells.get("B7").putValue(entity.getSubcontractCompanyName());

        // 第8行：纳税人识别号
        cells.get("B8").putValue(entity.getTaxNo());

        // 第9行：是否评价、是否需要CMA、是否开具（发票）
        cells.get("B9").putValue(booleanToYesNo(entity.getIsSubcontractCompany()));
        cells.get("D9").putValue(booleanToYesNo(entity.getIsCMA()));
        cells.get("F9").putValue(booleanToYesNo(entity.getIsInvoice()));

        // 第11行：是否签订合同、是否开口合同、是否现金
        cells.get("B10").putValue(booleanToYesNo(entity.getIsContract()));
        cells.get("D10").putValue(booleanToYesNo(entity.getIsOpenContract()));
        cells.get("F11").putValue(booleanToYesNo(entity.getIsCash()));

        // 第12行：计划送样日期、计划验收日期、计划交付日期
        if (entity.getPlanSendSampleDate() != null) {
            cells.get("B11").putValue(entity.getPlanSendSampleDate().toString());
        }
        if (entity.getPlanAcceptDate() != null) {
            cells.get("D11").putValue(entity.getPlanAcceptDate().toString());
        }
        if (entity.getPlanCompletionDate() != null) {
            cells.get("F11").putValue(entity.getPlanCompletionDate().toString());
        }

        // 第13行：交付内容
        if (entity.getReportDelivery() != null && !entity.getReportDelivery().isEmpty()) {
            cells.get("B12").putValue(String.join("、", entity.getReportDelivery()));
        }

        // 第14行：备注
        cells.get("B13").putValue(entity.getRemarks());

        // 第15行：样品清单（文件名）
        if (entity.getUploadSampleList() != null && !entity.getUploadSampleList().isEmpty()) {
            String fileNames = entity.getUploadSampleList().stream()
                    .map(SystemFile::getOriginalName)
                    .collect(java.util.stream.Collectors.joining("、"));
            cells.get("B14").putValue(fileNames);
        } else {
            cells.get("B14").putValue("无附件");
        }

        // 第16行：上传合同（文件名）
        if (entity.getContractFile() != null && !entity.getContractFile().isEmpty()) {
            String contractFileNames = entity.getContractFile().stream()
                    .map(SystemFile::getOriginalName)
                    .collect(java.util.stream.Collectors.joining("、"));
            cells.get("B15").putValue(contractFileNames);
        } else {
            cells.get("B15").putValue("无附件");
        }

        List<FlowOperationLog> logs = flowOperationLogService.findLogsByEntityId(entity.getId());
        createApprovalLog(worksheet, cells, logs);
    }

    public void createSummarySheet(Worksheet worksheet, Cells cells, List<SbctSummaryData> summaryData) {
        if (summaryData == null || summaryData.isEmpty()) {
            return;
        }
        //标题
        cells.get(0, 0).putValue("项目编号");
        cells.get(0, 1).putValue("检测编号");
        cells.get(0, 2).putValue("检测项目名称");
        cells.get(0, 3).putValue("样品数量");

    }

    /**
     * 样品清单sheet
     *
     * @param worksheet
     * @param samples:  样品列表
     */
    public void createSampleSheet(Worksheet worksheet, List<SubcontractTestingSample> samples) {
        if (samples == null || samples.isEmpty()) {
            return;
        }
        Cells cells = worksheet.getCells();
        //标题
        cells.get(0, 0).putValue("项目编号");
        cells.get(0, 1).putValue("检测编号");
        cells.get(0, 2).putValue("检测项目名称");
        cells.get(0, 3).putValue("井号");

        //数据
        int rowIdx = 1;
        for (SubcontractTestingSample sample : samples) {
            cells.get(rowIdx, 0).putValue(sample.getEntrustId());
            cells.get(rowIdx, 1).putValue(sample.getNewSampleNo());
            cells.get(rowIdx, 2).putValue(sample.getCheckModuleName());
            cells.get(rowIdx, 3).putValue(sample.getWellNo());
            rowIdx++;
        }
    }

    /**
     * 审批记录
     */
    private void createApprovalLog(Worksheet worksheet, Cells cells, List<FlowOperationLog> logs) {
        //logs 按时间排序
        logs.sort(Comparator.comparing(FlowOperationLog::getTaskStartTime));
        // 获取excel注释
        final CommentCollection comments = worksheet.getComments();
        int startRow = -1;
        if (comments != null) {
            for (int i = 0; i < comments.getCount(); i++) {
                final Comment comment = comments.get(i);
                String note = comment.getNote();
                if (note != null && note.contains("log_start")) {
                    startRow = comment.getRow();
                }
            }
        }
        if (startRow < 0) {
            return;
        }
        startRow = startRow + 1;
        for (FlowOperationLog log : logs) {
            cells.get("A" + startRow).putValue(log.getTaskName());
            cells.get("B" + startRow).putValue(log.getOperatorName());
            cells.get("C" + startRow).putValue(log.getTaskResult());
            cells.get("D" + startRow).putValue(log.getComment());
            cells.get("E" + startRow).putValue(TimeUtil.toYYYY_MM_DDString(log.getTaskEndTime()));
            startRow++;
        }
    }


    /**
     * 将Boolean类型转换为"是"或"否"
     *
     * @param value Boolean值
     * @return "是"或"否"
     */
    private String booleanToYesNo(Boolean value) {
        if (value == null) {
            return "否";
        } else {
            return value ? "是" : "否";
        }
    }


    @Override
    public String importBySamples(List<ImportSample> list)  {
        //1. 校验
        //1.1 校验必填项是否为空, 并指出是哪一行的
        list.forEach(i -> {
            if (StringUtils.isBlank(i.getProjectNo())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行项目编号不能为空，请检查项目编号是否正确");
            }
            if (StringUtils.isBlank(i.getSampleNo())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行检测编号不能为空，请检查检测编号是否正确");
            }
            if (StringUtils.isBlank(i.getCheckModuleId())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行检测项目编码不能为空，请检查检测项目编码是否正确");
            }
            //单价
            if (StringUtils.isBlank(i.getPrice())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行单价不能为空，请检查单价是否正确");
            }
            // 单价是否位数字
            try {
                new BigDecimal(i.getPrice());
            } catch (NumberFormatException e) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行单价(" + i.getPrice() + ")不是数字，请检查单价是否正确");
            }
        });

        // 写入数据库
        String tempId = UUID.randomUUID().toString();
        List<StlmTestTemp> stlmTestTemps = new ArrayList<>();
        for (ImportSample sample : list) {
            StlmTestTemp stlmTestTemp = StlmTestTemp.from(sample, tempId);
            stlmTestTemps.add(stlmTestTemp);
        }
        stlmTestTempRepository.saveAllAndFlush(stlmTestTemps);
        
        return tempId;
    }
}
