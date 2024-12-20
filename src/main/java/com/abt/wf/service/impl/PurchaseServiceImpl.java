package com.abt.wf.service.impl;

import com.abt.common.ExcelUtil;
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
import com.abt.wf.entity.UserSignature;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PurchaseService;
import com.abt.wf.service.SignatureService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

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


    @Value("${abt.pur.excel.template}")
    private String excelTemplate;

    @Value("${abt.pur.accept.excel.template}")
    private String acceptExcelTemplate;

    private static String acceptItems = "品名、规格、等级、生产日期、有效期、成分、包装、外观、贮存、数量、合格证明、说明书、保修卡";


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
        if (main == null) {
            throw new BusinessException("未查询到采购申请(id=" + entityId + ")");
        }
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
        final Page<PurchaseApplyMain> paged = purchaseApplyMainRepository.findAllByQueryPaged(null,
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
        final List<PurchaseApplyMain> list = purchaseApplyMainRepository.findUserTodoList(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), requestForm.getTaskDefKey());
        list.forEach(this::buildActiveTask);
        return list;
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

    @Override
    public PurchaseApplyMain clearBizProcessData(PurchaseApplyMain entity) {
        entity.setAccepted(false);
        entity.setManagerUserid(null);
        entity.setLeaderUserid(null);
        if (entity.getDetails() != null) {
            entity.getDetails().forEach(PurchaseApplyDetail::clearModify);
        }
        return entity;
    }

    @Override
    public void accept(PurchaseApplyMain form) {
        form.qualified(acceptItems);
        saveEntity(form);
    }

    @Override
    public File createPdf(String id, String pdfPath) throws Exception {
        final PurchaseApplyMain main = load(id);
        if (main == null) {
            throw new BusinessException("未找到采购申请单(审批编号: " + id + ")");
        }
        final File excel = createExcel(main, pdfPath);
        return ExcelUtil.excel2Pdf(excel.getAbsolutePath(), pdfPath);
    }

    @Override
    public File createExcel(String id, String path) throws Exception {
        final PurchaseApplyMain main = load(id);
        if (main == null) {
            throw new BusinessException("未找到采购申请单(审批编号: " + id + ")");
        }
        return createExcel(main, path);
    }

    /**
     * 生成excel文件
     * @return 返回生成文件的路径
     */
    public File createExcel(PurchaseApplyMain form, String pdfPath) throws IOException {
        //数据list
        List<PurchaseApplyDetail> list = form.getDetails();
        if (list == null) {
            list = new ArrayList<>();
        }
        //填充map
        Map<String, Object> map = new HashMap<>();
        map.put("createDate", TimeUtil.toYYYY_MM_DDString(form.getCreateDate()));
        map.put("managerUpdate", TimeUtil.toYYYY_MM_DDString(form.getManagerCheckDate()));
        map.put("leaderUpdate", TimeUtil.toYYYY_MM_DDString(form.getLeaderCheckDate()));
        if (form.getManagerCheckDate() != null) {
            map.put("managerSig", Objects.requireNonNullElse(setExcelSig(form.getManagerUserid()), "/"));
        }
        if (form.getLeaderCheckDate() != null) {
            map.put("leaderSig", Objects.requireNonNullElse(setExcelSig(form.getLeaderUserid()), "/"));
        }
        map.put("createUserSig", setExcelSig(form.getCreateUserid()));
        map.put("total", form.getCost());
        File newFile = new File(pdfPath);
        try (ExcelWriter excelWriter = EasyExcel.write(newFile).withTemplate(excelTemplate).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

            excelWriter.fill(list, fillConfig, writeSheet);
            excelWriter.fill(map, writeSheet);
        }
        return newFile;
    }

    private WriteCellData<Void> setExcelSig(String userid) throws IOException {
        if (StringUtils.isNotBlank(userid)) {
            final UserSignature msig = signatureService.getSignatureByUserid(userid);
            if (msig != null) {
                File sf = new File(signatureService.getSignatureDir() + msig.getFileName());
                return createImageData(sf);
            }
        }
        return null;
    }

    private WriteCellData<Void> createImageData(File imageFile) throws IOException {
        WriteCellData<Void> writeCellData = new WriteCellData<>();
        ImageData imageData = new ImageData();
        imageData.setImage(Files.readAllBytes(imageFile.toPath()));
        imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
        imageData.setTop(10);
        imageData.setBottom(10);
        imageData.setLeft(10);
        imageData.setRight(10);
        writeCellData.setImageDataList(List.of(imageData));
        return writeCellData;
    }

    @Override
    public void setBusinessId(String procId, String entityId) {
        runtimeService.setVariable(procId, PurchaseApplyMain.KEY_BIZ_ID, entityId);
    }

    @Override
    public void setMangerUser(String userid, PurchaseApplyMain form) {
        form.setManagerUserid(userid);
        form.setManagerCheckDate(LocalDateTime.now());
    }

    @Override
    public void setLeaderUser(String userid, PurchaseApplyMain form) {
        form.setLeaderUserid(userid);
        form.setLeaderCheckDate(LocalDateTime.now());
    }

    @Override
    public void delete(String id) {
        purchaseApplyMainRepository.deleteById(id);
    }

    @Override
    public File createAcceptExcel(PurchaseApplyMain form, String path) throws IOException {
        List<PurchaseApplyDetail> details = form.getDetails();
        if (details == null) {
            details = new ArrayList<>();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("acceptDate", TimeUtil.toYYYY_MM_DDString(form.getAcceptDate()));
        //采购人
        map.put("purchaserSig", Objects.requireNonNullElse(setExcelSig(form.getPurchaser()), ""));
        //验收人
        map.put("createUserSig", Objects.requireNonNullElse(setExcelSig(form.getCreateUserid()), ""));
        File file = new File(path);
        try (ExcelWriter excelWriter = EasyExcel.write(file).withTemplate(acceptExcelTemplate).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

            excelWriter.fill(details, fillConfig, writeSheet);
            excelWriter.fill(map, writeSheet);
        }
        return file;
    }


}
