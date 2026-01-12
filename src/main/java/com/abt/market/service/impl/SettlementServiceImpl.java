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
import com.abt.testing.entity.SampleRegist;
import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.repository.SampleRegistCheckModeuleItemRepository;
import com.abt.testing.repository.SampleRegistRepository;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.aspose.cells.*;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class SettlementServiceImpl implements SettlementService {
    private final SettlementMainRepository settlementMainRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final TestItemRepository testItemRepository;
    private final SettlementRelationRepository settlementRelationRepository;
    private final SaleAgreementRepository saleAgreementRepository;
    private final SettlementSummaryRepository settlementSummaryRepository;
    private final EntrustRepository entrustRepository;
    private final InvoiceApplyRepository invoiceApplyRepository;
    private final SampleRegistRepository sampleRegistRepository;
    private final SampleRegistCheckModeuleItemRepository sampleRegistCheckModeuleItemRepository;
    private final StlmSmryTempRepository stlmSmryTempRepository;

    private final StlmTestTempRepository stlmTestTempRepository;


    @Value("${abt.settlement.excel.template}")
    private String settlementTemplate;

    public SettlementServiceImpl(SettlementMainRepository settlementMainRepository, ExpenseItemRepository expenseItemRepository, TestItemRepository testItemRepository, SettlementRelationRepository settlementRelationRepository, SaleAgreementRepository saleAgreementRepository, SettlementSummaryRepository settlementSummaryRepository, EntrustRepository entrustRepository, InvoiceApplyRepository invoiceApplyRepository, SampleRegistRepository sampleRegistRepository, SampleRegistCheckModeuleItemRepository sampleRegistCheckModeuleItemRepository, StlmSmryTempRepository stlmSmryTempRepository, StlmTestTempRepository stlmTestTempRepository) {
        this.settlementMainRepository = settlementMainRepository;
        this.expenseItemRepository = expenseItemRepository;
        this.testItemRepository = testItemRepository;
        this.settlementRelationRepository = settlementRelationRepository;
        this.saleAgreementRepository = saleAgreementRepository;
        this.settlementSummaryRepository = settlementSummaryRepository;
        this.entrustRepository = entrustRepository;
        this.invoiceApplyRepository = invoiceApplyRepository;
        this.sampleRegistRepository = sampleRegistRepository;
        this.sampleRegistCheckModeuleItemRepository = sampleRegistCheckModeuleItemRepository;
        this.stlmSmryTempRepository = stlmSmryTempRepository;
        this.stlmTestTempRepository = stlmTestTempRepository;
    }

    @Transactional
    @Override
    public void save(SettlementMain main) {
        if (!CollectionUtils.isEmpty(main.getSummaryTab())) {
            calculateBySummaryData(main);
        }
        final SettlementMain save = settlementMainRepository.save(main);
        final String id = save.getId();
        // 暂时去掉校验
//        final List<String> err = validateCheckModules(main.getSummaryTab());
//        if (!err.isEmpty()) {
//            String error = String.join(";\n", err);
//            throw new BusinessException(error);
//        }
        //summarytab和testItems至少有一个有数据
//        if (CollectionUtils.isEmpty(save.getExpenseItems()) && CollectionUtils.isEmpty(save.getSummaryTab())) {
//            throw new BusinessException("样品汇总或样品列表数据为空，请至少输入一种数据!");
//        }

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

        testItemRepository.deleteByMid(id);
        // 保存检测项目
        if (!CollectionUtils.isEmpty(main.getTestItems())) {
            testItemRepository.flush();
            for (TestItem testItem : main.getTestItems()) {
                testItem.setMid(id);
            }
            testItemRepository.saveAllAndFlush(main.getTestItems());
        } else if (StringUtils.isNotBlank(main.getTempSummaryId())) {
            //有临时表的id，复制临时表的数据
            testItemRepository.insertByStlmTestTemp(main.getTempSummaryId(), main.getId());
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
    }


    /**
     * 根据summaryTab计算所有数据
     */
    private void calculateBySummaryData(SettlementMain main) {
        Set<SettlementSummary> summaryTab = main.getSummaryTab();
        if (CollectionUtils.isEmpty(summaryTab)) {
            return;
        }
        // 根据SettlementSummary的amount计算合计金额
        BigDecimal grossAmount = BigDecimal.ZERO;
        for (SettlementSummary summary : summaryTab) {
            grossAmount = grossAmount.add(BigDecimal.valueOf(summary.getAmount()));
        }
        grossAmount = grossAmount.setScale(2, RoundingMode.HALF_UP);
        main.setGrossTestAmount(grossAmount);
        main.setDiscountAmount(0.00);
        main.setDiscountPercentage(0.00);
        main.setNetTestAmount(grossAmount);
        main.setTotalAmount(main.getNetTestAmount().add(main.getExpenseAmount()).setScale(2,  RoundingMode.HALF_UP));
    }


    /**
     * 校验检测项目
     *
     * @param summaryTab 汇总表
     */
    private List<String> validateCheckModules(Set<SettlementSummary> summaryTab) {
        if (CollectionUtils.isEmpty(summaryTab)) {
            return new ArrayList<>();
        }
        List<String> errors = new ArrayList<>();
        final Set<String> entrustIds = summaryTab.stream().map(SettlementSummary::getEntrustId).collect(Collectors.toSet());
        final Set<String> checkModuleIds = summaryTab.stream().map(SettlementSummary::getCheckModuleId).collect(Collectors.toSet());
        final Set<Tuple> checkModules = sampleRegistRepository.findDistinctCheckModulesByEntrustId(entrustIds);
        for (SettlementSummary summary : summaryTab) {
            String entrustNo = summary.getEntrustId();
            String checkModuleId = summary.getCheckModuleId();
            if (StringUtils.isEmpty(entrustNo)) {
                throw new BusinessException("汇总表：委托编号不能为空");
            }
//            if (StringUtils.isEmpty(checkModuleId)) {
//                throw new BusinessException("汇总表：检测项目代码不能为空");
//            }

            //不在校验是否关联检测项目，有checkModuleId的检查是否一致
            if (StringUtils.isNotBlank(checkModuleId)) {
                final Optional<Tuple> any = checkModules.stream().filter(i -> entrustNo.equals(i.get("entrustId")) && checkModuleId.equals(i.get("CheckModeuleId"))).findAny();
                if (any.isEmpty()) {
                    errors.add(String.format("委托单%s没有检测项目:[%s(%s)]，请先添加检测项目", entrustNo, summary.getCheckModuleName(), checkModuleId));
                }
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
//        if (!main.isEntrustMode()) {
//            return null;
//        }
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
            if (!CollectionUtils.isEmpty(settlementMain.getTestItems())) {
                createSampleListSheet(workbook, settlementMain);
            }
            createSummaryExcelSheet(settlementMain, workbook);
            workbook.save(outputStream, SaveFormat.XLSX);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("生成结算单Excel失败", e);
        }
    }

    /**
     * 汇总数据
     * @param main
     * @param workbook
     */
    public void createSummaryExcelSheet(SettlementMain main, Workbook workbook) {
        Worksheet worksheet = workbook.getWorksheets().add("汇总表");
        Cells cells = worksheet.getCells();
        // 设置页面设置
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        pageSetup.setOrientation(PageOrientationType.PORTRAIT);

        //标题样式
        Style headerStyle = workbook.createStyle();
        headerStyle.getFont().setBold(true);
        headerStyle.getFont().setSize(14);
        headerStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        headerStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        //标题:项目编号	检测项目编码	检测项目名称	实际样品数量	结算数量	单价	金额合计	备注
        cells.get(0, 0).putValue("项目编号");
        cells.get(0, 0).setStyle(headerStyle);
        cells.get(0, 1).putValue("检测项目编码");
        cells.get(0, 1).setStyle(headerStyle);
        cells.get(0, 2).putValue("检测项目名称");
        cells.get(0, 2).setStyle(headerStyle);
        cells.get(0, 3).putValue("实际样品数量");
        cells.get(0, 3).setStyle(headerStyle);
        cells.get(0, 4).putValue("结算数量");
        cells.get(0, 4).setStyle(headerStyle);
        cells.get(0, 5).putValue("单价");
        cells.get(0, 5).setStyle(headerStyle);
        cells.get(0, 6).putValue("金额合计");
        cells.get(0, 6).setStyle(headerStyle);
        cells.get(0, 7).putValue("备注");
        cells.get(0, 7).setStyle(headerStyle);

        //数据样式
        Style dataStyle = createDataStyle(workbook);

        //数据
        int dataRow = 1;
        for (SettlementSummary summary : main.getSummaryTab()) {
            cells.get(dataRow, 0).putValue(summary.getEntrustId());
            cells.get(dataRow, 0).setStyle(dataStyle);
            cells.get(dataRow, 1).putValue(summary.getCheckModuleId());
            cells.get(dataRow, 1).setStyle(dataStyle);
            cells.get(dataRow, 2).putValue(summary.getCheckModuleName());
            cells.get(dataRow, 2).setStyle(dataStyle);
            cells.get(dataRow, 3).putValue(summary.getTestNum());
            cells.get(dataRow, 3).setStyle(dataStyle);
            cells.get(dataRow, 4).putValue(summary.getSampleNum());
            cells.get(dataRow, 4).setStyle(dataStyle);
            cells.get(dataRow, 5).putValue(summary.getPrice());
            cells.get(dataRow, 5).setStyle(dataStyle);
            cells.get(dataRow, 6).putValue(summary.getAmount());
            cells.get(dataRow, 6).setStyle(dataStyle);
            cells.get(dataRow, 7).putValue(summary.getRemark());
            cells.get(dataRow, 7).setStyle(dataStyle);
            dataRow++;
        }
    }


    /**
     * 结算单页面
     *
     * @param settlementMain 结算单对象
     * @param workbook       workbook
     */
    public void createSummarySheet(SettlementMain settlementMain, Workbook workbook) {
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
        // 优惠
        if (settlementMain.getDiscountPercentage() != null) {
            cells.insertRow(currentRow);
            String money = MoneyUtil.toUpperCase(settlementMain.getDiscountAmount().toString());
            String percentage = MoneyUtil.toPercentage(settlementMain.getDiscountPercentage(), 2);
            cells.get(currentRow, 0).putValue(String.format("优惠(%s): %s", percentage, money));
            cells.get(currentRow, 0).setStyle(dataStyle);
            cells.get(currentRow, sumAmountCol).putValue(settlementMain.getDiscountAmount());
            cells.get(currentRow, sumAmountCol).setStyle(dataStyle);
            worksheet.getCells().merge(currentRow, 0, 1, sumAmountCol);
            currentRow++;
        }

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
     *
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
            if (invList != null && !invList.isEmpty()) {
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

    @Transactional
    @Override
    public void updateSaveType(SaveType saveType, String id) {
        settlementMainRepository.updateSaveType(saveType, id);
    }


    @Transactional
    @Override
    public String importBySamples(List<ImportSample> list) {
        //1. 校验  
        //1.1 必填列不能为空
        list.forEach(i -> {
            //1.1 项目编号不能存在空的，并指出是哪一行的
            if (StringUtils.isBlank(i.getProjectNo())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行项目编号不能为空，请检查项目编号是否正确");
            }

            //1.2 检测编号不能存在空的，并指出是哪一行的
            if (StringUtils.isBlank(i.getSampleNo())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行检测编号不能为空，请检查检测编号是否正确");
            }

            //1.3 检测项目代码不能存在空的，并指出是哪一行的
            if (StringUtils.isBlank(i.getCheckModuleId())) {
                throw new BusinessException("第" + (list.indexOf(i) + 1) + "行检测项目编码不能为空，请检查检测项目编码是否正确");
            }

            //1.4 单价是否为数字
            if (StringUtils.isNotBlank(i.getPrice())) {
                try {
                    new BigDecimal(i.getPrice());
                } catch (NumberFormatException e) {
                    throw new BusinessException("第" + (list.indexOf(i) + 1) + "行单价(" + i.getPrice() + ")不是数字，请检查单价是否正确");
                }
            }
        });


        //1.2 项目编号是否存在，并指出哪个不存在
        // 提取所有的项目编号（去重）
        Set<String> projectNos = list.stream()
                .map(ImportSample::getProjectNo)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        // 查询数据库中存在的项目
        List<Entrust> existingEntrusts = entrustRepository.findByIdIn(projectNos);
        Set<String> existingProjectNos = existingEntrusts.stream()
                .map(Entrust::getId)
                .collect(Collectors.toSet());
        // 找出不存在的项目编号
        Set<String> notExistingProjectNos = projectNos.stream()
                .filter(projectNo -> !existingProjectNos.contains(projectNo))
                .collect(Collectors.toSet());

        // 如果有不存在的项目编号，抛出异常
        if (!notExistingProjectNos.isEmpty()) {
            String missingProjects = String.join("、", notExistingProjectNos);
            throw new BusinessException("以下项目编号不存在: " + missingProjects + ",请检测项目编号是否正确或录入项目");
        }

        //1.2 检测编号是否存在
        Set<String> sampleNos = list.stream()
                .map(ImportSample::getSampleNo)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        List<SampleRegist> existingSampleRegists = sampleRegistRepository.findByNewSampleNoIn(sampleNos);
        Set<String> existingSampleNos = existingSampleRegists.stream()
                .map(SampleRegist::getNewSampleNo)
                .collect(Collectors.toSet());
        Set<String> notExistingSampleNos = sampleNos.stream()
                .filter(sampleNo -> !existingSampleNos.contains(sampleNo))
                .collect(Collectors.toSet());
        if (!notExistingSampleNos.isEmpty()) {
            String missingSamples = String.join("、", notExistingSampleNos);
            throw new BusinessException("以下检测编号不存在: " + missingSamples + ",请检查检测编号是否正确或录入检测编号");
        }

        //1.3 样品的检测项目是否存在
        //提取所有检测项目code
        Set<String> checkModuleIds = list.stream()
                .map(ImportSample::getCheckModuleId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        //查询数据库中存在的检测项目
        List<SampleRegistCheckModeuleItem> existingSampleRegistCheckModeuleItems = sampleRegistCheckModeuleItemRepository.findByCheckModeuleIdIn(checkModuleIds);
        Set<String> notExistingCheckModuleIds = existingSampleRegistCheckModeuleItems.stream()
                .map(SampleRegistCheckModeuleItem::getCheckModeuleId)
                .filter(checkModuleId -> !checkModuleIds.contains(checkModuleId))
                .collect(Collectors.toSet());
        if (!notExistingCheckModuleIds.isEmpty()) {
            String missingCheckModuleIds = String.join("、", notExistingCheckModuleIds);
            throw new BusinessException("以下检测项目代码不存在: " + missingCheckModuleIds + ",请检查检测项目代码是否正确或录入检测项目");
        }

        //2. 写入数据库
        //2.1 写入stlm_test_temp表
        String tempMid = UUID.randomUUID().toString();
        List<StlmTestTemp> stlmTestTemps = new ArrayList<>();
        int idx = 0;
        for (ImportSample sample : list) {
            StlmTestTemp stlmTestTemp = StlmTestTemp.from(sample, tempMid);
            //关联样品信息
            existingSampleRegists.stream().filter(i -> i.getNewSampleNo().equals(sample.getSampleNo())).findFirst().ifPresent(i -> {
                stlmTestTemp.setOldSampleNo(i.getOldSampleNo());
                stlmTestTemp.setWellNo(i.getJname());
            });
            stlmTestTemp.setSortNo(idx);
            stlmTestTemps.add(stlmTestTemp);
            idx++;
        }
        stlmTestTempRepository.saveAllAndFlush(stlmTestTemps);

        //2.2 生成并写入stlm_smry_temp表
        stlmSmryTempRepository.createSummaryByTestItemTemp(tempMid);
        return tempMid;
    }


    @Transactional
    @Override
    public void deleteTempByTempMid(String tempMid) {
        stlmTestTempRepository.deleteAllByTempMid(tempMid);
    }

    @Override
    public Page<StlmTestTemp> findTestTempByQuery(TestTempRequestForm requestForm) {
        final PageRequest pageable = requestForm.createPageable(Sort.by("sampleNo", "checkModuleId"));
        // 使用Example查询
        StlmTestTemp exampleEntity = new StlmTestTemp();
        if (StringUtils.isNotBlank(requestForm.getTempMid())) {
            exampleEntity.setTempMid(requestForm.getTempMid());
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()  // 忽略null值，只匹配非null字段
                .withIgnoreCase();       // 忽略大小写
        Example<StlmTestTemp> example = Example.of(exampleEntity, matcher);

        return stlmTestTempRepository.findAll(example, pageable);
    }

    @Override
    public List<SettlementSummary> createSummaryTableByTestTemp(String tempMid) {
        final List<StlmTestTemp> list = stlmTestTempRepository.findAllByTempMid(tempMid);

        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        return stlmTestTempRepository.createSummaryDataByTemp(tempMid);
    }

    /**
     * 导出委托单样品到Excel
     *
     * @param items        样品检测项目列表
     * @param outputStream 输出流
     * @throws Exception 导出异常
     */
    @Override
    public void exportTestTempToExcel(List<SampleRegistCheckModeuleItem> items, OutputStream outputStream) throws Exception {
        //创建工作簿
        Workbook workbook = new Workbook();
        //创建工作表
        Worksheet worksheet = workbook.getWorksheets().get(0);
        //写入表头：项目编号，检测编号，检测项目编码，检测项目名称，单价，备注
        worksheet.getCells().get(0, 0).putValue("项目编号");
        worksheet.getCells().get(0, 1).putValue("检测编号");
        worksheet.getCells().get(0, 2).putValue("检测项目编码");
        worksheet.getCells().get(0, 3).putValue("检测项目名称");
        worksheet.getCells().get(0, 4).putValue("单价");
        worksheet.getCells().get(0, 5).putValue("备注");
        //表头样式，加粗，黑色
        Style headerStyle = workbook.createStyle();
        headerStyle.getFont().setBold(true);
        headerStyle.getFont().setSize(14);
        headerStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        headerStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        worksheet.getCells().get(0, 0).setStyle(headerStyle);
        worksheet.getCells().get(0, 1).setStyle(headerStyle);
        worksheet.getCells().get(0, 2).setStyle(headerStyle);
        worksheet.getCells().get(0, 3).setStyle(headerStyle);
        worksheet.getCells().get(0, 4).setStyle(headerStyle);
        worksheet.getCells().get(0, 5).setStyle(headerStyle);

        //写入数据
        //数据样式
        Style dataStyle = workbook.createStyle();
        dataStyle.getFont().setSize(11);
        dataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        dataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        dataStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        dataStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        dataStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        dataStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        int rowIdx = 1;
        for (SampleRegistCheckModeuleItem item : items) {
            worksheet.getCells().get(rowIdx, 0).putValue(item.getEntrustId());
            worksheet.getCells().get(rowIdx, 0).setStyle(dataStyle);
            worksheet.getCells().get(rowIdx, 1).putValue(item.getSampleRegistId());
            worksheet.getCells().get(rowIdx, 1).setStyle(dataStyle);
            worksheet.getCells().get(rowIdx, 2).putValue(item.getCheckModeuleId());
            worksheet.getCells().get(rowIdx, 2).setStyle(dataStyle);
            worksheet.getCells().get(rowIdx, 3).putValue(item.getCheckModeuleName());
            worksheet.getCells().get(rowIdx, 3).setStyle(dataStyle);
            worksheet.getCells().get(rowIdx, 4).putValue("");
            worksheet.getCells().get(rowIdx, 4).setStyle(dataStyle);
            worksheet.getCells().get(rowIdx, 5).putValue("");
            worksheet.getCells().get(rowIdx, 5).setStyle(dataStyle);
            rowIdx++;
        }

        //保存excel到输出流
        workbook.save(outputStream, SaveFormat.XLSX);
    }


    @Override
    public String importBySummaryData(MultipartFile file) throws Exception {

        //1. 读取数据，使用aspose.cell
        Workbook workbook = new Workbook(file.getInputStream());
        Worksheet worksheet = workbook.getWorksheets().get(0);
        Cells cells = worksheet.getCells();
        //2. 校验标题列是否正确
        //2.1 项目编号列
        validateTitleColumn(cells, "项目编号", 0, 0);
        //2.3 检测项目编码列
        validateTitleColumn(cells, "检测项目编码", 0, 1);
        //2.4 检测项目名称列
        validateTitleColumn(cells, "检测项目名称", 0, 2);
        //实际检测样品数量
        validateTitleColumn(cells, "实际样品数量", 0, 3);
        //2.4 结算数量列/甲方认定数量
        validateTitleColumn(cells, "结算数量", 0, 4);
        //2.5 单价列
        validateTitleColumn(cells, "单价", 0, 5);
        //2.6 金额合计
        validateTitleColumn(cells, "金额合计", 0, 6);
        //3. 读取数据
        List<StlmSmryTemp> summaryList = new ArrayList<>();
        String tempId = UUID.randomUUID().toString();
        for (int rowIdx = 1; rowIdx < cells.getRows().getCount(); rowIdx++) {
            StlmSmryTemp summary = new StlmSmryTemp();
            //项目编号必填
            String entrustId = cells.get(rowIdx, 0).getStringValue().trim();
            if (StringUtils.isEmpty(entrustId)) {
                throw new BusinessException(String.format("第%d行项目编号为空", rowIdx));
            }
            String checkModuleId = cells.get(rowIdx, 1).getStringValue().trim();
            String checkModuleName = cells.get(rowIdx, 2).getStringValue().trim();
            // 检测项目编码和名称至少有一个
            if (StringUtils.isEmpty(checkModuleId) && StringUtils.isEmpty(checkModuleName)) {
                throw new BusinessException(String.format("第%d行检测项目编码和名称不能同时为空", rowIdx));
            }
            //实际样品数量
            String testNum = cells.get(rowIdx, 3).getStringValue().trim();
            if (StringUtils.isEmpty(testNum)) {
                throw new BusinessException(String.format("第%d行实际样品数量为空", rowIdx));
            }
            // 结算数量必须有
            String sampleNum = cells.get(rowIdx, 4).getStringValue().trim();
            if (StringUtils.isEmpty(sampleNum)) {
                throw new BusinessException(String.format("第%d行结算数量为空", rowIdx));
            }
            // 单价必须填
            String price = cells.get(rowIdx, 5).getStringValue().trim();
            if (StringUtils.isBlank(price)) {
                throw new BusinessException(String.format("第%d行单价为空", rowIdx));
            }
            // 校验是否是数字
            validateNumber(testNum, String.format("第%d行%s(%s)不是数字，请检查", rowIdx, "单价", testNum));
            // 金额合计必须填
            String amount = cells.get(rowIdx, 6).getStringValue().trim();
            // 校验是否是数字
            if (StringUtils.isBlank(amount)) {
                throw new BusinessException(String.format("第%d行金额合计为空", rowIdx));
            }
            validateNumber(testNum, String.format("第%d行%s(%s)不是数字，请检查", rowIdx, "金额合计", testNum));
            summary.setEntrustId(entrustId);
            summary.setCheckModuleId(checkModuleId);
            summary.setCheckModuleName(checkModuleName);
            summary.setTestNum(Integer.parseInt(testNum));
            summary.setSampleNum(Integer.parseInt(sampleNum));
            summary.setPrice(new BigDecimal(price));
            summary.setAmount(new BigDecimal(amount));
            summary.setSortNo(rowIdx);
            summary.setMid(tempId);
            summary.setRemark(cells.get(rowIdx, 7).getStringValue().trim());
            summaryList.add(summary);
        }
        //4. 保存数据
        stlmSmryTempRepository.saveAllAndFlush(summaryList);
        return tempId;
    }

    private void validateNumber(String text, String message) {
        try {
            new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new BusinessException(message);
        }
    }


    private void validateTitleColumn(Cells cells, String name, int rowIdx, int colIdx) {
        Cell cell = cells.get(rowIdx, colIdx);
        String columnName = cell.getStringValue().trim();
        if (!columnName.equals(name)) {
            throw new BusinessException(String.format("标题(%s:[%d,%d])不正确，请检查标题列是否正确", columnName, rowIdx, colIdx));
        }
    }


    @Override
    public List<StlmSmryTemp> getTempSummaryData(String tempId) {
        return stlmSmryTempRepository.findByMidOrderBySortNo(tempId);
    }

    @Override
    public void logicDelete(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("结算单号不能为空!");
        }
        settlementMainRepository.logicDelete(id);
    }

    /**
     * 导入样品-校验是否重复结算
     * @param tempId 临时导入数据mid
     * @return 重复的样品信息
     */
    public List<ValidateDuplicatedSampleDTO> validateDuplicatedSampleByTempId(String tempId) {
        final List<ValidateDuplicatedSampleDTO> list = testItemRepository.checkTempDuplicatedSamples(tempId);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().filter(ValidateDuplicatedSampleDTO::isDuplicated).toList();
    }


    private String getValidateKey(String sampleNo, String checkModuleId) {
        return sampleNo + "_" + checkModuleId;
    }

    /**
     * 根据testItems校验重复结算
     * @param items 传入的testItems
     */
    public List<ValidateDuplicatedSampleDTO> validateDuplicatedSampleByItems(List<TestItem> items) {
        List<ValidateDuplicatedSampleDTO> list = new  ArrayList<>();
        Set<String> entrustIds = items.stream().map(TestItem::getEntrustId).collect(Collectors.toSet());
        final List<TestItem> pool = testItemRepository.findSettledSamplesByEntrustIdIn(entrustIds);
        for (TestItem item : items) {

            String tempKey = getValidateKey(item.getSampleNo(), item.getCheckModuleId());
            pool.stream().filter(i -> tempKey.equals(getValidateKey(i.getEntrustId(), item.getCheckModuleId()))).findFirst().
                    ifPresent(i -> {
                        // 有重复的
                        ValidateDuplicatedSampleDTO dto = new ValidateDuplicatedSampleDTO();
                        dto.setTempSampleNo(item.getSampleNo());
                        dto.setTempCheckModuleId(item.getCheckModuleId());
                        dto.setTempCheckModuleName(item.getCheckModuleName());
                        dto.setSampleNo(i.getSampleNo());
                        dto.setCheckModuleId(item.getCheckModuleId());
                        dto.setCheckModuleName(item.getCheckModuleName());
                        dto.setMid(item.getMid());
                        dto.setTestItemId(item.getId());
                        list.add(dto);
                    });
        }


        return list;
    }

}
