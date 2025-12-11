package com.abt.wf.service.impl;

import com.abt.common.ExcelUtil;
import com.abt.common.model.RequestForm;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.listener.PurchaseNameMergeHandler;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.PurchaseService;
import com.abt.wf.service.SignatureService;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.metadata.data.ImageData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.runtime.ProcessInstance;
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
import java.io.OutputStream;
import java.math.BigInteger;
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

    private final BpmnModelInstance purchaseBpmnModelInstance;

    private final IFileService fileService;
    private final HistoryService historyService;
    private final SignatureService signatureService;

    private final PurchaseApplyMainRepository purchaseApplyMainRepository;


    @Value("${abt.pur.excel.template}")
    private String excelTemplate;

    @Value("${abt.pur.accept.excel.template}")
    private String acceptExcelTemplate;

    public static final String LOGIN_USER = "loginUser";

    private static final String acceptItems = "品名、规格、等级、生产日期、有效期、成分、包装、外观、贮存、数量、合格证明、说明书、保修卡";


    public PurchaseServiceImpl(IdentityService identityService, RepositoryService repositoryService, RuntimeService runtimeService,
                               TaskService taskService, FlowOperationLogService flowOperationLogService,
                               @Qualifier("sqlServerUserService") UserService userService,
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
        final PurchaseApplyMain main = load(entityId);
        return this.simpleProcessRecord(entityId, serviceName, main.getProcessInstanceId());
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
        final List<PurchaseApplyDetail> details = main.getDetails();

        if (details != null && !details.isEmpty()) {
            details.sort((a, b) -> {
                if (a.getSortNo() == null || b.getSortNo() == null) {
                    return 0;
                }
                return a.getSortNo().compareTo(b.getSortNo());
            });
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
        if (LOGIN_USER.equals(requestForm.getUserid())) {
            //查询登录用户的
            requestForm.setUserid(TokenUtil.getUseridFromAuthToken());
        }
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
        if (!entity.isFinished() || STATE_DETAIL_ACTIVE.equals(entity.getBusinessState())) {
            throw new BusinessException(String.format("采购流程(%s)正在审批中，无法重新提交", id));
        }
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
        final File excel = doCreateExcel(main, pdfPath);
        return ExcelUtil.excel2Pdf(excel.getAbsolutePath(), pdfPath);
    }

    @Override
    public File createExcel(String id, String path) throws Exception {
        final PurchaseApplyMain main = load(id);
        if (main == null) {
            throw new BusinessException("未找到采购申请单(审批编号: " + id + ")");
        }
        return doCreateExcel(main, path);
    }

    /**
     * 生成excel文件
     * @return 返回生成文件的路径
     */
    public File doCreateExcel(PurchaseApplyMain form, String pdfPath) throws IOException {
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
        map.put("purchaserUpdate", TimeUtil.toYYYY_MM_DDString(form.getPurchaserCheckDate()));
        map.put("ceoUpdate", TimeUtil.toYYYY_MM_DDString(form.getCeoCheckDate()));
        if (form.getManagerCheckDate() != null) {
            map.put("managerSig", Objects.requireNonNullElse(setExcelSig(form.getManagerUserid()), "/"));
        }
        if (form.getLeaderCheckDate() != null) {
            map.put("leaderSig", Objects.requireNonNullElse(setExcelSig(form.getLeaderUserid()), "/"));
        }
        if (form.getPurchaserCheckDate() != null) {
            final WriteCellData<Void> writeCellData = setExcelSig(form.getPurchaser());
            //设置合并单元格
            final ImageData imageData = writeCellData.getImageDataList().get(0);
            imageData.setRelativeFirstRowIndex(0);
            imageData.setRelativeFirstColumnIndex(0);
            imageData.setRelativeLastRowIndex(0);
            imageData.setRelativeLastColumnIndex(1);
            map.put("purchaserSig", writeCellData);
        }
        if (form.getCeoCheckDate() != null) {
            map.put("ceoSig", Objects.requireNonNullElse(setExcelSig(form.getCeo()), "/"));
        }

        map.put("createUserSig", setExcelSig(form.getCreateUserid()));
        map.put("total", form.getCost());
        File newFile = new File(pdfPath);
        try (ExcelWriter excelWriter = EasyExcel.write(newFile).withTemplate(excelTemplate).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(new PurchaseNameMergeHandler()).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(list, fillConfig, writeSheet);
            excelWriter.fill(map, writeSheet);
        }
        return newFile;
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
    public void setCeoUser(String userid, PurchaseApplyMain form) {
        form.setCeo(userid);
        form.setCeoCheckDate(LocalDateTime.now());
    }

    @Override
    public void delete(String id) {
        final PurchaseApplyMain main = purchaseApplyMainRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到流程(id=" + id + ")"));
        purchaseApplyMainRepository.deleteById(id);
        //删除正在进行的流程
        final ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().active().processInstanceId(main.getProcessInstanceId()).singleResult();
        if (processInstance != null) {
            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "用户" + TokenUtil.getUserFromAuthToken().getName() + "手动删除流程");
        }

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

    @Override
    public void createPurchaseListExcel(List<PurchaseApplyMain> list, OutputStream outputStream) throws Exception {
        //使用aspose创建excel
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);
        Cells cells = worksheet.getCells();
        //表格标题: 序号	审批编号	总金额	状态	申请人	部门	创建时间	是否验收	验收时间	物品名称	规格型号	单位	采购数量	单价	用途
        Style tableHeaderStyle = workbook.createStyle();
        tableHeaderStyle.getFont().setBold(true);
        tableHeaderStyle.getFont().setSize(12);
        tableHeaderStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        tableHeaderStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        cells.insertRow(0);
        cells.get(0, 0).putValue("序号");
        cells.get(0, 0).setStyle(tableHeaderStyle);
        cells.get(0, 1).putValue("审批编号");
        cells.get(0, 1).setStyle(tableHeaderStyle);
        cells.get(0, 2).putValue("总金额");
        cells.get(0, 2).setStyle(tableHeaderStyle);
        cells.get(0, 3).putValue("状态");
        cells.get(0, 3).setStyle(tableHeaderStyle);
        cells.get(0, 4).putValue("申请人");
        cells.get(0, 4).setStyle(tableHeaderStyle);
        cells.get(0, 5).putValue("部门");
        cells.get(0, 5).setStyle(tableHeaderStyle);
        cells.get(0, 6).putValue("创建时间");
        cells.get(0, 6).setStyle(tableHeaderStyle);
        cells.get(0, 7).putValue("验收时间");
        cells.get(0, 7).setStyle(tableHeaderStyle);
        cells.get(0, 8).putValue("是否验收");
        cells.get(0, 8).setStyle(tableHeaderStyle);
        cells.get(0, 9).putValue("物品名称");
        cells.get(0, 9).setStyle(tableHeaderStyle);
        cells.get(0, 10).putValue("规格型号");
        cells.get(0, 10).setStyle(tableHeaderStyle);
        cells.get(0, 11).putValue("单位");
        cells.get(0, 11).setStyle(tableHeaderStyle);
        cells.get(0, 12).putValue("采购数量");
        cells.get(0, 12).setStyle(tableHeaderStyle);
        cells.get(0, 13).putValue("单价");
        cells.get(0, 13).setStyle(tableHeaderStyle);
        cells.get(0, 14).putValue("用途");
        cells.get(0, 14).setStyle(tableHeaderStyle);

        //表格数据
        int dataRow = 1;
        //数据样式
        Style dataStyle = workbook.createStyle();
        dataStyle.getFont().setSize(10);
        dataStyle.getFont().setColor(Color.getBlack());
        int seq = 1;
        for (PurchaseApplyMain main : list) {
            int detailCount = main.getDetails() != null ? main.getDetails().size() : 0;
            if (detailCount == 0) {
                detailCount = 1;
            }
            // 记录起始行，用于后续合并单元格
            int startRow = dataRow;
            // 插入需要的行数
            for (int r = 0; r < detailCount; r++) {
                cells.insertRow(dataRow + r);
            }
            //1. 序号
            cells.get(dataRow, 0).putValue(seq);
            cells.get(dataRow, 0).setStyle(dataStyle);
            //2. 审批编号
            cells.get(dataRow, 1).putValue(main.getId());
            cells.get(dataRow, 1).setStyle(dataStyle);
            //3. 总金额
            cells.get(dataRow, 2).putValue(main.getCost());
            cells.get(dataRow, 2).setStyle(dataStyle);
            //4. 状态
            cells.get(dataRow, 3).putValue(main.getBusinessState());
            cells.get(dataRow, 3).setStyle(dataStyle);
            //5. 申请人
            cells.get(dataRow, 4).putValue(main.getCreateUsername());
            cells.get(dataRow, 4).setStyle(dataStyle);
            //6. 部门
            cells.get(dataRow, 5).putValue(main.getCreateDeptName());
            cells.get(dataRow, 5).setStyle(dataStyle);
            //7. 创建时间
            cells.get(dataRow, 6).putValue(TimeUtil.toYYYY_MM_DDString(main.getCreateDate()));
            cells.get(dataRow, 6).setStyle(dataStyle);
            //8. 验收时间
            cells.get(dataRow, 7).putValue(TimeUtil.toYYYY_MM_DDString(main.getAcceptDate()));
            cells.get(dataRow, 7).setStyle(dataStyle);
            //9. 是否验收
            cells.get(dataRow, 8).putValue(main.isAccepted() ? "是" : "否");
            cells.get(dataRow, 8).setStyle(dataStyle);
            // 采购详情
            if (main.getDetails() != null && main.getDetails().size() > 0) {
                for (PurchaseApplyDetail detail : main.getDetails()) {
                    //物品名称
                    cells.get(dataRow, 9).putValue(detail.getName());
                    cells.get(dataRow, 9).setStyle(dataStyle);
                    //规格型号
                    cells.get(dataRow, 10).putValue(detail.getSpecification());
                    cells.get(dataRow, 10).setStyle(dataStyle);
                    //单位
                    cells.get(dataRow, 11).putValue(detail.getUnit());
                    cells.get(dataRow, 11).setStyle(dataStyle);
                    //采购数量
                    cells.get(dataRow, 12).putValue(detail.getQuantity());
                    cells.get(dataRow, 12).setStyle(dataStyle);
                    //单价
                    cells.get(dataRow, 13).putValue(detail.getPrice());
                    cells.get(dataRow, 13).setStyle(dataStyle);
                    //用途
                    cells.get(dataRow, 14).putValue(detail.getUsage());
                    cells.get(dataRow, 14).setStyle(dataStyle);
                    dataRow++;
                }
            } else {
                dataRow++;
            }
            // 对0-8列根据采购详情中物品数量进行合并行（当详情数量大于1时才需要合并）
            if (detailCount > 1) {
                for (int col = 0; col <= 8; col++) {
                    cells.merge(startRow, col, detailCount, 1);
                    // 设置合并后单元格的样式（垂直居中）
                    Style mergeStyle = cells.get(startRow, col).getStyle();
                    mergeStyle.setVerticalAlignment(TextAlignmentType.CENTER);
                    cells.get(startRow, col).setStyle(mergeStyle);
                }
            }
            seq++;
        }
        //保存excel，并提供web下载
        workbook.save(outputStream, SaveFormat.XLSX);
    }


}
