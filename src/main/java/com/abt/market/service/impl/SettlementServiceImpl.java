package com.abt.market.service.impl;

import com.abt.common.util.MoneyUtil;
import com.abt.common.util.TokenUtil;
import com.abt.market.entity.*;
import com.abt.market.model.*;
import com.abt.market.repository.*;
import com.abt.market.service.SettlementService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.testing.entity.Entrust;
import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.repository.SampleRegistRepository;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.aspose.cells.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
  *
  */
@Service
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    @PersistenceContext
    private EntityManager entityManager;

    private final SettlementMainRepository  settlementMainRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final TestItemRepository testItemRepository;
    private final SettlementRelationRepository settlementRelationRepository;
    private final SaleAgreementRepository saleAgreementRepository;
    private final SettlementSummaryRepository settlementSummaryRepository;
    private final EntrustRepository entrustRepository;
    private final InvoiceApplyRepository invoiceApplyRepository;
    private final SampleRegistRepository sampleRegistRepository;


    @Value("${abt.settlement.excel.template}")
    private String settlementTemplate;

    public SettlementServiceImpl(SettlementMainRepository settlementMainRepository, ExpenseItemRepository expenseItemRepository, TestItemRepository testItemRepository, SettlementRelationRepository settlementRelationRepository, SaleAgreementRepository saleAgreementRepository, SettlementSummaryRepository settlementSummaryRepository, EntrustRepository entrustRepository, InvoiceApplyRepository invoiceApplyRepository, SampleRegistRepository sampleRegistRepository) {
        this.settlementMainRepository = settlementMainRepository;
        this.expenseItemRepository = expenseItemRepository;
        this.testItemRepository = testItemRepository;
        this.settlementRelationRepository = settlementRelationRepository;
        this.saleAgreementRepository = saleAgreementRepository;
        this.settlementSummaryRepository = settlementSummaryRepository;
        this.entrustRepository = entrustRepository;
        this.invoiceApplyRepository = invoiceApplyRepository;
        this.sampleRegistRepository = sampleRegistRepository;
    }

    @Transactional
    @Override
    public void save(SettlementMain main) {
        final SettlementMain save = settlementMainRepository.save(main);
        final String id = save.getId();


        final List<String> err = validateCheckModules(main.getSummaryTab());
        if (!err.isEmpty()) {
            String error = String.join(";\n", err);
            throw new BusinessException(error);
        }

        if (main.isEntrustMode()) {
            testItemRepository.deleteByMid(id);
            testItemRepository.flush();
            final List<TestItem> itemList = relateSamples(main);
            Set<TestItem> set = new HashSet<>(itemList);
            main.setTestItems(set);
        }

        main.calculateAllAmount();
        
        // 保存检测项目
        if (main.getTestItems() != null) {
            testItemRepository.deleteByMid(id);
            testItemRepository.flush();

            for (TestItem testItem : main.getTestItems()) {
                testItem.setMid(id);
            }
            testItemRepository.saveAllAndFlush(main.getTestItems());
        }
        
        // 保存其他费用
        if (main.getExpenseItems() != null) {
            expenseItemRepository.deleteByMid(id);
            expenseItemRepository.flush();
            // 按sortNo排序，确保保存时维持用户输入的顺序
            for (ExpenseItem expenseItem : main.getExpenseItems()) {
                expenseItem.setMid(id);
            }
            expenseItemRepository.saveAllAndFlush(main.getExpenseItems());
        }
        
        // 保存关联关系
        if (main.getRelations() != null) {
            settlementRelationRepository.deleteByMid(id);
            settlementRelationRepository.flush();
            for (SettlementRelation relation : main.getRelations()) {
                relation.setMid(id);
            }
            settlementRelationRepository.saveAllAndFlush(main.getRelations());
        }
        
        // 保存汇总表数据
        if (main.getSummaryTab() != null && !main.getSummaryTab().isEmpty()) {
            // 先删除原有的汇总数据
            settlementSummaryRepository.deleteByMid(id);
            settlementSummaryRepository.flush();
            
            // 保存新的汇总数据
            for (SettlementSummary summary : main.getSummaryTab()) {
                summary.setMid(id);
            }
            settlementSummaryRepository.saveAllAndFlush(main.getSummaryTab());
        }

    }

    /**
     * 校验检测项目
     * 1. 对应委托单中是否有该检测项目
     * 2. 检测项目是否存在
     * @param summaryTab 汇总表
     */
    private List<String> validateCheckModules(Set<SettlementSummary> summaryTab) {
        if (CollectionUtils.isEmpty(summaryTab)) {
            return new ArrayList<>();
        }
        List<String> errors = new ArrayList<>();
        final Set<String> entrustIds = summaryTab.stream().map(SettlementSummary::getEntrustId).collect(Collectors.toSet());
        final Set<Tuple> checkModules = sampleRegistRepository.findDistinctCheckModulesByEntrustId(entrustIds);
        for (SettlementSummary summary : summaryTab) {
            String entrustNo = summary.getEntrustId();
            String checkModuleId = summary.getCheckModuleId();
            if (StringUtils.isEmpty(entrustNo)) {
                throw new BusinessException("汇总表：委托编号不能为空");
            }
            if (StringUtils.isEmpty(checkModuleId)) {
                throw new BusinessException("汇总表：检测项目代码不能为空");
            }

            final Optional<Tuple> any = checkModules.stream().filter(i -> entrustNo.equals(i.get("entrustId")) && checkModuleId.equals(i.get("CheckModeuleId")))
                    .findAny();
            if (any.isEmpty()) {
                errors.add(String.format("委托单%s没有检测项目:[%s](%s)", entrustNo, summary.getCheckModuleName(), checkModuleId));
            }
        }
        return errors;
    }

    private String genKey(String entrustId, String checkModuleId) {
        return String.format("%s_%s", entrustId, checkModuleId);
    }

    /**
     * 自动关联样品
     *
     * @param main 结算主体
     * @return List<TestItem> TestItem 结算的样品
     */
    public List<TestItem> relateSamples(SettlementMain main) {
        if (!main.isEntrustMode()) {
            return null;
        }
        final Set<SettlementSummary> summaryTab = main.getSummaryTab();
        Set<String> entrustIds = summaryTab.stream().map(SettlementSummary::getEntrustId).collect(Collectors.toSet());
        final Map<String, SettlementSummary> map = summaryTab.stream().collect(Collectors.toMap(st -> genKey(st.getEntrustId(), st.getCheckModuleId()), st -> st));

        // 查询所有项目的未结算样品及检测项目
        final List<TestItem> unsettledSampleNoList = findUnsettledTestItems(entrustIds);
        if (CollectionUtils.isEmpty(unsettledSampleNoList)) {
            throw new BusinessException("未选择待结算的样品和检测项目");
        }

         // 校验每个检测项目的未结算样品是否不小于提交的数量
        final Map<String, List<TestItem>> tiMap = unsettledSampleNoList.stream()
                .collect(Collectors.groupingBy(ti -> genKey(ti.getEntrustId(), ti.getCheckModuleId()), Collectors.toList()));
        List<String> error = new ArrayList<>();
        tiMap.forEach((key, tiList) -> {
            final SettlementSummary summary = map.get(key);
            if (summary != null) {
                // 结算数量大于未结算的
                if (summary.getSampleNum() > tiList.size()) {
                    error.add(String.format("委托编号：%s的[%s]未结算数量%s小于提交数量%s", summary.getEntrustId(), summary.getCheckModuleName(), tiList.size(), summary.getSampleNum()));
                }
            }
        });
        if (!error.isEmpty()) {
            String msg = String.join(",\n", error);
            throw new BusinessException(msg);
        }

        List<TestItem> savedList = new ArrayList<>();
        for (Map.Entry<String, List<TestItem>> entry : tiMap.entrySet()) {
            String key = entry.getKey();
            final List<TestItem> itemList = entry.getValue();
            itemList.sort(Comparator.comparing(TestItem::getSampleNo));
            final SettlementSummary summary = map.get(key);
            if (summary != null) {
                final int sampleNum = summary.getSampleNum();
                savedList.addAll(itemList.subList(0, sampleNum));
            }
        }

        // 单价，单位
        for (TestItem testItem : savedList) {
            String key = genKey(testItem.getEntrustId(), testItem.getCheckModuleId());
            final SettlementSummary summary = map.get(key);
            if (summary != null) {
                testItem.setPrice(BigDecimal.valueOf(summary.getPrice()));
                testItem.setSampleUnit(summary.getUnit());
                testItem.setMid(main.getId());
            }
        }
        return savedList;
    }

    private List<TestItem> findUnsettledTestItems(Set<String> entrustIds) {
        final List<Tuple> tuples = testItemRepository.findUnsettledSamples(entrustIds);
        if (CollectionUtils.isEmpty(tuples)) {
            return new ArrayList<>();
        }
        List<TestItem> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
            TestItem ti = new TestItem();
            ti.setEntrustId(tuple.get("entrust_id").toString());
            ti.setSampleNo(tuple.get("sample_no").toString());
            ti.setCheckModuleId(tuple.get("check_module_id").toString());
            ti.setCheckModuleName(tuple.get("check_module_name").toString());
            ti.setOldSampleNo(tuple.get("old_sample_no").toString());
            ti.setWellNo(tuple.get("well_no").toString());
            list.add(ti);
        }
        return list;
    }

    @Override
    public SettlementMain findSettlementMainOnly(String id) {
        Objects.requireNonNull(id, "结算单ID不能为空");
        return settlementMainRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到结算单(id=" + id + ")"));
    }

    @Override
    public SettlementMain findSettlementMainWithAllItems(String id) {
        Objects.requireNonNull(id, "结算单ID不能为空");
        final SettlementMain main = settlementMainRepository.findOneWithAllItems(id);
        
        // 加载关联合同
        final List<String> agreementIds = main.getAgreementIds();
        final List<SaleAgreement> agreements = saleAgreementRepository.findByIdIsIn(agreementIds);
        main.setSaleAgreements(agreements);

        //关联开票
        final List<InvoiceApply> inv = invoiceApplyRepository.findRefSettlement(main.getId());
        main.setInvoiceApply(inv);

        return main;
    }

    @Transactional
    @Override
    public void delete(String id) {
        settlementMainRepository.deleteById(id);
        testItemRepository.deleteByMid(id);
        expenseItemRepository.deleteByMid(id);
        settlementRelationRepository.deleteByMid(id);
        // 删除汇总表数据
        settlementSummaryRepository.deleteByMid(id);
    }

    // 复杂查询使用CriteriaBuilder真的不行，特别是还带分页
    @Override
    public Page<SettlementMainListDTO> findMainOnlyByQuery(SettlementRequestForm requestForm, Pageable pageable) {
        final Page<SettlementMainListDTO> page = settlementMainRepository.findMainOnlyByQuery(
                StringUtils.trim(requestForm.getQuery()),
                requestForm.getLocalStartDate(),
                requestForm.getLocalEndDate(),
                StringUtils.trim(requestForm.getTestLike()),
                requestForm.getClientId(),
                requestForm.getState(),
                pageable
        );
        // 关联开票
        final List<SettlementMainListDTO> list = page.getContent();
        if (!list.isEmpty()) {
            final Set<String> idSet = list.stream().map(SettlementMainListDTO::getId).collect(Collectors.toSet());
            // 关联的发票
            final List<InvoiceApply> invs = invoiceApplyRepository.findBySettlementIdIn(idSet);
            for (SettlementMainListDTO dto : list) {
                String mid = dto.getId();
                final List<InvoiceApply> invList = invs.stream().filter(i -> i.getSettlementId().equals(mid)).toList();
                //计算开票金额和
                if (!invList.isEmpty()) {
                    final double sum = invList.stream().map(InvoiceApply::getInvoiceAmount).mapToDouble(Double::doubleValue).sum();
                    dto.setInvoiceAmount(sum);
                    dto.setIsIssued(Boolean.TRUE);
                } else {
                    dto.setIsIssued(Boolean.FALSE);
                }
            }
        }

        return page;

    }

    @Override
    public void createSettlementExcel(SettlementMain settlementMain, OutputStream outputStream) {
        try {
            // 从模板文件读取工作簿
            Workbook workbook = new Workbook(settlementTemplate);
            createSummarySheet(settlementMain, workbook);
            createSampleListSheet(workbook, settlementMain);
            workbook.save(outputStream, SaveFormat.XLSX);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("生成结算单Excel失败", e);
        }

    }


    /**
     * 结算单页面
     * @param settlementMain 结算单对象
     * @param workbook workbook
     */
    public void createSummarySheet(SettlementMain settlementMain,  Workbook workbook) {
        // 从模板文件读取工作簿
        Worksheet worksheet = workbook.getWorksheets().get(0);
        Cells cells = worksheet.getCells();
        // 设置页面设置
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        pageSetup.setOrientation(PageOrientationType.PORTRAIT);

        // 创建样式
        Style dataStyle = createDataStyle(workbook);
        Style dateStyle = createDateStyle(workbook);

        int headerRow = 3;
        int dataRow = 6;
        int sumAmountCol = 4;

        //-- 客户名称
        String clientName = settlementMain.getClientName().replaceAll("\\(开票信息\\)", "").replaceAll("（开票信息）", "");
        cells.get(headerRow, 0).putValue(clientName + ":");

        //--- 汇总数据
        int currentRow = dataRow;

        if (settlementMain.getSummaryTab() != null && !settlementMain.getSummaryTab().isEmpty()) {
            for (SettlementSummary summaryRow : settlementMain.getSummaryTab()) {
                cells.insertRow(currentRow);
                // 序号
                cells.get(currentRow, 0).putValue(summaryRow.getSortNo());
                cells.get(currentRow, 0).setStyle(dataStyle);

                // 测试项目
                cells.get(currentRow, 1).putValue(summaryRow.getCheckModuleName());
                cells.get(currentRow, 1).setStyle(dataStyle);

                // 数量
                cells.get(currentRow, 2).putValue(summaryRow.getSampleNum());
                cells.get(currentRow, 2).setStyle(dataStyle);

                // 单价
                cells.get(currentRow, 3).putValue(summaryRow.getPrice());
                cells.get(currentRow, 3).setStyle(dataStyle);

                // 合计
                cells.get(currentRow, 4).putValue(summaryRow.getAmount());
                cells.get(currentRow, 4).setStyle(dataStyle);

                // 备注： 项目编号
                cells.get(currentRow, 5).putValue(summaryRow.getEntrustId());
                cells.get(currentRow, 5).setStyle(dataStyle);
                currentRow++;
            }
        }

        //----- 合计行
        // 合计行-其他费用
        if (settlementMain.getExpenseItems() != null && !settlementMain.getExpenseItems().isEmpty()) {
            for (ExpenseItem expenseItem : settlementMain.getExpenseItems()) {
                cells.insertRow(currentRow);
                cells.get(currentRow, 0).putValue(expenseItem.getName());
                cells.get(currentRow, 0).setStyle(dataStyle);
                cells.get(currentRow, sumAmountCol).putValue(expenseItem.getAmount());
                cells.get(currentRow, sumAmountCol).setStyle(dataStyle);
                worksheet.getCells().merge(currentRow, 0, 1, sumAmountCol);
                currentRow++;
            }
        }
        // 合计行--总计
        cells.insertRow(currentRow);
        String smryLabel = "合计: ";
        String money = MoneyUtil.toUpperCase(settlementMain.getFinalAmount().toString());
        if (settlementMain.isTax() && settlementMain.getTaxRate() != null) {
            smryLabel = String.format("合计(含税%.2f%%): %s", settlementMain.getTaxRate() * 100, money);
        } else {
            smryLabel = "合计(不含税): " + money;
        }
        cells.get(currentRow, 0).putValue(smryLabel);
        cells.get(currentRow, 0).setStyle(dataStyle);
        cells.get(currentRow, sumAmountCol).putValue(settlementMain.getFinalAmount());
        cells.get(currentRow, sumAmountCol).setStyle(dataStyle);
        worksheet.getCells().merge(currentRow, 0, 1, sumAmountCol);
//        // 日期和签名
//        LocalDate now = LocalDate.now();
//        String dateStr = String.format("%d 年 %d 月 %d 日", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
//        cells.get(currentRow, 0).putValue(dateStr);
//        cells.get(currentRow, 4).setStyle(dateStyle);
    }
    /**
     * 创建数据样式
     */
    private Style createDataStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.getFont().setName("宋体");
        style.getFont().setSize(11);
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        return style;
    }

    private Style createDateStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.getFont().setName("宋体");
        style.getFont().setSize(10);
        style.setHorizontalAlignment(TextAlignmentType.RIGHT);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        return style;
    }
    /**
     * 创建右对齐样式
     */
    private Style createRightAlignStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.getFont().setName("宋体");
        style.getFont().setSize(11);
        style.setHorizontalAlignment(TextAlignmentType.RIGHT);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        return style;
    }

    /**
     * 导出样品详情表
     * @param workbook workbook
     */
    public void createSampleListSheet(Workbook workbook, SettlementMain stlmMain) throws Exception {
        final Worksheet worksheet = workbook.getWorksheets().get(1);
        Cells cells = worksheet.getCells();
        final Set<TestItem> testItems = stlmMain.getTestItems();
        if (testItems == null || testItems.isEmpty()) {
            return;
        }
        final Set<String> entrustIds = testItems.stream().map(TestItem::getEntrustId).collect(Collectors.toSet());
        final List<Entrust> ents = entrustRepository.findByIdIn(entrustIds);
        final Map<String, Entrust> entMap = ents.stream().collect(Collectors.toMap(Entrust::getId, e -> e));
        stlmMain.getTestItems().forEach(item -> {
            Entrust entrust = entMap.get(item.getEntrustId());
            if (entrust != null) {
                item.setEntrust(entMap.get(item.getEntrustId()));
            }

        });

        WorkbookDesigner designer = new WorkbookDesigner();
        designer.setWorkbook(workbook);
        designer.setDataSource("list", stlmMain.getTestItems());
        designer.process(true);
    }


    @Override
    public int saveRef(List<SettlementRelation> srs, String mid) {
        final long count = settlementMainRepository.countById(mid);
        if (count > 0) {
            final List<SettlementRelation> settlementRelations = settlementRelationRepository.saveAllAndFlush(srs);
            return settlementRelations.size();
        } else {
            log.warn("未找到结算单(id={})", mid);
            return 0;
        }
    }

    @Override
    public void deleteRefByBizType(String mid, SettlementRelationType bizType) {
        if (bizType == null) {
            settlementRelationRepository.deleteByMid(mid);
        } else {
            settlementRelationRepository.deleteByMidAndBizType(mid, bizType);
        }
    }

    @Transactional
    @Override
    public void deleteRefBy(String mid, String rid) {
        settlementRelationRepository.deleteByMidAndRid(mid, rid);
    }

    @Override
    public void invalid(SettlementMain main) {
        final SaveType saveType = main.getSaveType();
        //1. 只能自己作废
        final UserView user = TokenUtil.getUserFromAuthToken();
        if (!user.getId().equals(main.getCreateUserid())) {
            throw new BusinessException(String.format("只有创建人(%s)可以作废该结算单(%s)", main.getCreateUsername(), main.getId()));
        }
        if (SaveType.SAVE == saveType) {
            //1. 查找开票
            final List<Double> invList = settlementRelationRepository.findInvoiceApplyByMid(main.getId());
            //2. 开票金额合计是否为0，先这样简单处理
            if (invList != null &&!invList.isEmpty()) {
                final double sum = invList.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
                if (sum != 0.00) {
                    // 合计不为0，不能作废
                    throw new BusinessException("开票金额合计不为0，请先提交开票申请冲账");
                }
            }
            main.setSaveType(SaveType.INVALID);
            settlementMainRepository.save(main);

        } else if (SaveType.TEMP == saveType) {
            main.setSaveType(SaveType.INVALID);
            settlementMainRepository.save(main);
        }
    }

    @Override
    public List<CustomerInfo> getClients() {
        return settlementMainRepository.getAllCustomers();
    }


    @Transactional
    @Override
    public void updateRelations(RelationRequest relationRequest) {
        deleteRefByBizType(relationRequest.getMid(), relationRequest.getBizType());
        saveRef(relationRequest.getRelationList(), relationRequest.getMid());
    }
}
