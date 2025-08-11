package com.abt.material.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.material.entity.*;
import com.abt.material.listener.ImportCheckBillListener;
import com.abt.material.listener.ImportInventoryAlertListener;
import com.abt.material.model.*;
import com.abt.material.repository.*;
import com.abt.material.service.StockService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.util.WithQueryUtil;
import cn.idev.excel.EasyExcel;
import com.abt.wf.generator.CommonIdGenerator;
import com.abt.wf.model.PurchaseSummaryAmount;
import com.abt.wf.repository.PurchaseApplyDetailRepository;
import com.aspose.cells.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abt.material.entity.StockOrder.*;
import static java.util.stream.Collectors.collectingAndThen;

/**
 * 出入库
 */
@Service
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockOrderRepository stockOrderRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialDetailRepository materialDetailRepository;
    private final InventoryRepository inventoryRepository;
    private final MaterialTypeRepository materialTypeRepository;
    private final InventoryAlertRepository inventoryAlertRepository;
    private final PurchaseApplyDetailRepository purchaseApplyDetailRepository;

    @Value("${abt.stock.export.week.template}")
    private String stockWeekTemplate;

    @Value("${abt.stock.export.week.path}")
    private String stockWeekFilePath;


    public StockServiceImpl(StockOrderRepository stockOrderRepository, StockRepository stockRepository, WarehouseRepository warehouseRepository, MaterialDetailRepository materialDetailRepository, InventoryRepository inventoryRepository, MaterialTypeRepository materialTypeRepository, InventoryAlertRepository inventoryAlertRepository, PurchaseApplyDetailRepository purchaseApplyDetailRepository, CommonIdGenerator commonIdGenerator) {
        this.stockOrderRepository = stockOrderRepository;
        this.stockRepository = stockRepository;
        this.warehouseRepository = warehouseRepository;
        this.materialDetailRepository = materialDetailRepository;
        this.inventoryRepository = inventoryRepository;
        this.materialTypeRepository = materialTypeRepository;
        this.inventoryAlertRepository = inventoryAlertRepository;
        this.purchaseApplyDetailRepository = purchaseApplyDetailRepository;
    }

    @Transactional
    @Override
    public StockOrder saveStockOrder(StockOrder stockOrder) {
        stockOrder = stockOrderRepository.save(stockOrder);
        if (stockOrder.getStockList() != null) {
            for (Stock stock : stockOrder.getStockList()) {
                stock.setOrderId(stockOrder.getId());
                //库存变化
                Inventory inv = new Inventory();
                inv.setWarehouseId(stockOrder.getWarehouseId());
                inv.setMaterialId(stock.getMaterialId());
                if (STOCK_TYPE_CHECK != stockOrder.getStockType()) {
                    //当前库存
                    inv = inventoryRepository.findOneLatestInventory(stock.getMaterialId(), stockOrder.getWarehouseId())
                            .orElse(new Inventory(stock.getMaterialId(), stockOrder.getWarehouseId()));
                }
                Inventory newInv = changeInventoryQuantity(inv, stock, stockOrder.getStockType());
                newInv.setOrderId(stockOrder.getId());
                //必须每次单独保存
                inventoryRepository.saveAndFlush(newInv);
                stockRepository.saveAndFlush(stock);
            }
        }
        return stockOrder;
    }

    private Double getQuantity(Double quantity) {
        return quantity == null ? 0 : quantity;
    }

    private Inventory changeInventoryQuantity(Inventory inventory, Stock stock, int stockType) {
        Inventory newInventory = new Inventory();
        newInventory.setMaterialId(inventory.getMaterialId());
        newInventory.setWarehouseId(inventory.getWarehouseId());
        if (STOCK_TYPE_IN == stockType) {
            newInventory.setQuantity(getQuantity(inventory.getQuantity()) + stock.getNum());
        } else if (STOCK_TYPE_OUT == stockType) {
            newInventory.setQuantity(getQuantity(inventory.getQuantity()) - stock.getNum());
        } else if (STOCK_TYPE_CHECK == stockType) {
            newInventory.setQuantity(stock.getNum());
        } else {
            log.warn("Stock type {} not supported", stockType);
        }
        return newInventory;
    }

    @Override
    public StockOrder findDetailsByOrderId(String orderId) {
        return stockOrderRepository.findByIdWithAllDetail(orderId).orElseThrow(() -> new BusinessException("未查询到出入库单据信息"));
    }

    @Override
    public Page<Stock> findStocksByQueryPageable(StockOrderRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getSize());
        requestForm.buildForm();
        final Page<Stock> page = stockRepository.findByQueryPageable(requestForm.getQuery(),
                requestForm.getStockType(),
                requestForm.getWarehouseIds(),
                null,
                TimeUtil.toLocalDate(requestForm.getStartDate()),
                TimeUtil.toLocalDate(requestForm.getEndDate()),
                pageable);
        WithQueryUtil.build(page);
        return page;
    }


    @Override
    public void saveWarehouse(Warehouse warehouse) {
        //名称不能重复
        String name = warehouse.getName();
        final List<Warehouse> list = warehouseRepository.findByName(name);
        if (!list.isEmpty()) {
            throw new BusinessException("库房名称不能重复(库房名称: " + name + " 已存在)");
        }
        warehouseRepository.save(warehouse);
    }

    @Override
    public List<Warehouse> findAllWarehouseBy(WarehouseRequestForm requestForm) {
        Warehouse criteria = new Warehouse();
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues();
        if (requestForm.isQueryAll()) {
            matcher.withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<Warehouse> example = Example.of(criteria, matcher);
        return WithQueryUtil.build(warehouseRepository.findAll(example, Sort.by(Sort.Direction.ASC, "sortNo")));
    }

    @Override
    public Integer getWarehouseSortNoForNew() {
        return warehouseRepository.findMaxSortNo() + 1;
    }

    @Override
    public void deleteWarehouse(String id) {
        warehouseRepository.deleteById(id);
    }

    /**
     * 库存最新详情
     */
    @Override
    public Page<Inventory> latestInventories(InventoryRequestForm requestForm) {
        buildInventoryRequestForm(requestForm);
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getSize());
        return WithQueryUtil.build(inventoryRepository
                .findLatestInventory(requestForm.getMaterialTypeIds(), requestForm.getWarehouseIds(), requestForm.getName(), requestForm.isShowAlertOnly(), pageable));
    }

    private InventoryRequestForm buildInventoryRequestForm(InventoryRequestForm requestForm) {
        if (requestForm == null) {
            requestForm = new InventoryRequestForm();
        }
        //查询全部
        if (requestForm.getMaterialTypeIds() == null || requestForm.getMaterialTypeIds().isEmpty()) {
            requestForm.setMaterialTypeIds(List.of("all"));
        }
        //查询全部
        if (requestForm.getWarehouseIds() == null || requestForm.getWarehouseIds().isEmpty()) {
            requestForm.setWarehouseIds(List.of("all"));
        }
        return requestForm;
    }

    @Override
    public List<MaterialDetailDTO> findAllMaterialInventories(InventoryRequestForm requestForm) {
        requestForm = buildInventoryRequestForm(requestForm);
        final List<IMaterialDetailDTO> list = materialDetailRepository.findAllWithInventories(requestForm.getMaterialTypeIds(), requestForm.getWarehouseIds());
        List<MaterialDetailDTO> dtos = new ArrayList<>(list.size());
        for (IMaterialDetailDTO im : list) {
            MaterialDetailDTO d = new MaterialDetailDTO();
            d.setMaterialDetail(im.getMaterialDetail());
            d.setMaterialType(im.getMaterialDetail().getMaterialType());
            d.setInventory(im.getInventory());
            d.setWarehouse(im.getWarehouse());
            MaterialDetail dtl = im.getMaterialDetail();
            d.setId(dtl.getId());
            d.setName(dtl.getName());
            d.setSpecification(dtl.getSpecification());
            d.setUnit(dtl.getUnit());
            MaterialType type = dtl.getMaterialType();
            if (type != null) {
                d.setMaterialTypeId(type.getId());
                d.setMaterialTypeName(type.getName());
            }
            Warehouse wh = im.getWarehouse();
            if (wh != null) {
                d.setWarehouseId(wh.getId());
                d.setWarehouseName(wh.getName());
            }
            if (im.getInventory() != null) {
                d.setCurrentInventory(im.getInventory().getQuantity());
            }
            dtos.add(d);
        }
        return dtos;
    }

    @Transactional
    @Override
    public StockOrder importCheckBill(File file, StockOrder order) {
        List<MaterialDetailDTO> list = new ArrayList<>();
        List<MaterialDetailDTO> errorList = new ArrayList<>();
        EasyExcel.read(file, MaterialDetailDTO.class, new ImportCheckBillListener(order, this, list, errorList)).sheet().headRowNumber(3).doRead();
        order.setMaterialDetailDTOList(list);
        order.setErrorList(errorList);
        order.setHasError(!errorList.isEmpty());
        return order;
    }

    @Override
    public void addStock(StockOrder order, MaterialDetailDTO dto) {
        order.addStock(convert(dto));
    }

    private Stock convert(MaterialDetailDTO dto) {
        Stock stock = new Stock();
        stock.setMaterialId(dto.getId());
        stock.setMaterialName(dto.getName());
        stock.setSpecification(dto.getSpecification());
        stock.setUnit(dto.getUnit());
        stock.setRemark(dto.getRemark());
        stock.setNum(dto.getCheckInventory());
        stock.setBizType("盘点库存");
        stock.setStockType(STOCK_TYPE_CHECK);
        stock.setMaterialTypeId(dto.getMaterialTypeId());
        stock.setMaterialTypeName(dto.getMaterialTypeName());
        stock.setRemark(dto.getRemark());

        return stock;
    }

    @Override
    public List<InventoryAlert> findInventoryAlertList(InventoryRequestForm requestForm) {
        return WithQueryUtil.build(inventoryAlertRepository.findAllBy(requestForm.getWarehouseIds(), requestForm.getName()));
    }

    @Override
    public Page<InventoryAlert> findInventoryAlertPageable(InventoryRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.asc("materialType.name"), Sort.Order.asc("warehouse.name"), Sort.Order.asc("materialDetail.name")));
        final Page<InventoryAlert> page = inventoryAlertRepository.findByQueryPageable(requestForm.getWarehouseIds(), requestForm.getName(), pageable);
        return WithQueryUtil.build(page);
    }

    @Override
    public Page<StockOrder> findStockOrderPageable(StockOrderRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("orderDate")));
        final Page<StockOrder> page = stockOrderRepository.findPageable(requestForm.getStockType(), requestForm.getStartDate(), requestForm.getEndDate(), pageable);
        WithQueryUtil.build(page);
        return page;
    }

    @Override
    public List<MaterialType> findAllMaterialType(MaterialTypeRequestForm requestForm) {
        return materialTypeRepository.findByQuery(requestForm.getIds(), requestForm.getName(), requestForm.getIsDeleted());
    }

    @Transactional
    @Override
    public void hardDeleteCheckBill(String id) {
        //1. inventory
        inventoryRepository.deleteByOrderId(id);
        //2. stock order
        stockRepository.deleteByOrderId(id);
        stockOrderRepository.deleteById(id);
    }


    @Override
    public void checkImportData(MaterialDetailDTO row) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(row.getId())) {
            sb.append("物品id不能为空!");
        }
        if (StringUtils.isBlank(row.getWarehouseName())) {
            sb.append("仓库名称不能为空!");
        }
        if (StringUtils.isNotBlank(row.getRemark()) && row.getRemark().length() > 100) {
            sb.append("备注信息不可超过100字!");
        }
        if (row.getCheckInventory() == null) {
            sb.append("盘点库存不能为空!");
        }

        if (!sb.isEmpty()) {
            //返回row, 错误信息
            row.setError(sb.toString());
        }
    }

    @Override
    public void saveInventoryAlert(InventoryAlert inventoryAlert) {
        inventoryAlertRepository.save(inventoryAlert);
    }

    @Override
    public void saveInventoryAlertList(List<InventoryAlert> list) {
        //使用save默认update时，导致createDate, createUserid无数据。所以先删除
        inventoryAlertRepository.deleteAllInBatch(list);
        inventoryAlertRepository.saveAllAndFlush(list);
    }

    @Override
    public List<InventoryAlert> importInventoryAlertFile(File file) {
        List<InventoryAlert> list = new ArrayList<>();
        List<InventoryAlert> errorList = new ArrayList<>();
        final List<Warehouse> whList = findAllWarehouseBy(new WarehouseRequestForm());
        final Map<String, Warehouse> whMap = whList.stream().collect(Collectors.toMap(Warehouse::getName, w -> w));
        EasyExcel.read(file, InventoryAlert.class, new ImportInventoryAlertListener(list, this, errorList, whMap)).sheet().headRowNumber(2).doRead();
        if (!errorList.isEmpty()) {
            return errorList;
        } else {
            //保存
            inventoryAlertRepository.saveAllAndFlush(list);
            return List.of();
        }
    }

    @Override
    public void checkInventoryAlertData(InventoryAlert row, Map<String, Warehouse> whMap) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(row.getMaterialId())) {
            sb.append("物料id不能为空!");
        }
        if (StringUtils.isNotBlank(row.getWarehouseName()) && !whMap.containsKey(row.getWarehouseName())) {
            sb.append("库存名称不存在!");
        }
        if (!sb.isEmpty()) {
            row.setErrorMsg(sb.toString());
        }
    }

    @Override
    public List<InventoryAlert> getInventoryAlert(String mid) {
        List<InventoryAlert> list = inventoryAlertRepository.findById_MaterialId(mid);
        return WithQueryUtil.build(list);
    }

    @Override
    public List<PurchaseSummaryAmount> summaryPurchaseGiftTotalAmount(LocalDate startDate, LocalDate endDate) {
        return purchaseApplyDetailRepository.summaryGiftTotalAmount(GIFT_TYPE, startDate, endDate);
    }

    public static final String GIFT_TYPE = "礼品类";

    @Override
    public StockSummaryTable inventoryGiftDetails(LocalDate startDate, LocalDate endDate, StockSummaryTable table) {
        List<Stock> stockList = new ArrayList<>();
        StockOrderRequestForm requestForm = new StockOrderRequestForm();
        Pageable pageable = PageRequest.of(0, 9999);
        requestForm.buildForm();
        final Page<Stock> page1 = stockRepository.findByQueryPageable(null,
                1,
                requestForm.getWarehouseIds(),
                GIFT_TYPE,
                startDate,
                endDate,
                pageable);
        stockList.addAll(WithQueryUtil.build(page1.getContent()));
        final Page<Stock> page2 = stockRepository.findByQueryPageable(null,
                2,
                requestForm.getWarehouseIds(),
                GIFT_TYPE,
                startDate,
                endDate,
                pageable);
        stockList.addAll(WithQueryUtil.build(page2.getContent()));
        //剩余库存(endDate前最近日期的库存，包含endDate)
        final ArrayList<Stock> distinctList = stockList.stream().collect(collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(obj -> obj.getMaterialId() + "#" + obj.getWarehouseId())
                )),
                ArrayList::new
        ));

        Map<String, Inventory> invMap = new HashMap<>();

        for (Stock stock : distinctList) {
            final Inventory inv = inventoryRepository.findNearestBefore(endDate.plusDays(1L), stock.getMaterialId(), stock.getWarehouseId());
            invMap.put(stock.getMaterialId() + "#" + stock.getWarehouseId(), inv);
        }
        for (Stock stock : stockList) {
            final Inventory inv = invMap.get(stock.getMaterialId() + "#" + stock.getWarehouseId());
            if (inv != null) {
                stock.setInventory(inv.getQuantity());
            }
        }

        // 将stockList按warehouseId分组
        final Map<String, List<Stock>> map = stockList.stream()
                .collect(Collectors.groupingBy(Stock::getWarehouseId));
        for (Map.Entry<String, List<Stock>> entry : map.entrySet()) {
            final List<Stock> list = entry.getValue();
            list.sort(stockComparator());
        }
        table.setGiftStockMap(map);
        return table;
    }

    private Style createBaseStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.getFont().setName("SimSun");
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.setTextWrapped(true);
        return style;
    }

    private Comparator<Stock> stockComparator() {
        return Comparator.comparing(Stock::getMaterialName)
                .thenComparing(Stock::getSpecification)
                .thenComparing(Stock::getUnit)
                .thenComparing(Stock::getOrderDate);
    }

    @Override
    public void stockSummary(LocalDate startDate, LocalDate endDate, StockSummaryTable table) {
        final List<StockQuantitySummary> stockIn = stockRepository.summaryGiftQuantity(STOCK_TYPE_IN, startDate, endDate);
        final List<StockQuantitySummary> stockOut = stockRepository.summaryGiftQuantity(STOCK_TYPE_OUT, startDate, endDate);
        table.setStockInSummary(stockIn);
        table.setStockOutSummary(stockOut);
    }

    @Override
    public String createExcelWeek(StockSummaryTable summaryTable, LocalDate startDate, LocalDate endDate,
                                  Map<String, Warehouse> warehouseMap) throws Exception {
        Workbook workbook = new Workbook(stockWeekTemplate);
        Worksheet sheet = workbook.getWorksheets().get(0);
        //日期行
        sheet.getCells().get(1, 0).putValue(TimeUtil.toYYYY_MM_DDString(startDate) + "至" + TimeUtil.toYYYY_MM_DDString(endDate));
        Style dataStyle = dataStyle(workbook);

        final Map<String, List<Stock>> giftStockMap = summaryTable.getGiftStockMap();
        int startRow = 2;
        for (Map.Entry<String, List<Stock>> entry : giftStockMap.entrySet()) {
            String whid = entry.getKey();
            Warehouse wh =  warehouseMap.get(whid);
            String whName = "库房";
            if (wh != null) {
                whName = wh.getName();
            }
            final List<Stock> stockList = entry.getValue();
            int endRow = createStockTable(whName, startRow, stockList.stream().filter(i -> !"采购".equals(i.getBizType())).toList(), sheet, dataStyle);
            // 空行
            endRow++;
            sheet.getCells().insertRow(endRow);
            sheet.getCells().getRows().get(endRow).setHeight(24);
            startRow = startRow + endRow;
        }

        //本月采购
        final List<Integer> purchaseMonthGiftCell = getStockGiftRow(sheet, PURCHASE_MONTH_GIFT);
        if (purchaseMonthGiftCell.isEmpty()) {
            throw new BusinessException("模板无法匹配！purchase_month_gift注释不存在");
        }
        createPurchaseTable(purchaseMonthGiftCell.get(0), summaryTable.getPurchaseSummaryAmountList(), sheet, dataStyle);
        //合计
        final List<Integer> purchaseMonthSumCell = getStockGiftRow(sheet, PURCHASE_MONTH_SUM);
        if (purchaseMonthSumCell.isEmpty()) {
            throw new BusinessException("模板无法匹配！purchase_month_sum注释不存在");
        }
        final BigDecimal purSum = sumPurchases(summaryTable.getPurchaseSummaryAmountList());
        sheet.getCells().get(purchaseMonthSumCell.get(0), purchaseMonthSumCell.get(1)).putValue(purSum.setScale(2, RoundingMode.FLOOR).toString());

        // 本年采购
        final List<Integer> purchaseYearDate = getStockGiftRow(sheet, PURCHASE_YEAR_DATE);
        if (purchaseYearDate.isEmpty()) {
            throw new BusinessException("模板无法匹配！purchase_year_date注释不存在");
        }
        Style purchaseYearDateStyle = createBaseStyle(workbook);
        purchaseYearDateStyle.getFont().setSize(14);
        purchaseYearDateStyle.getFont().setBold(true);
        purchaseYearDateStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
        final int year = endDate.getYear();
        final LocalDate range1 = LocalDate.of(year, 1, 1);
        final Month month = endDate.getMonth();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate range2 = yearMonth.atEndOfMonth();
        String dateRange = String.format("%s 至 %s", range1.format(TimeUtil.dateFormatter), range2.format(TimeUtil.dateFormatter));
        sheet.getCells().get(purchaseYearDate.get(0), 0).putValue(String.format("本年采购情况(%s)", dateRange));
        // 本年采购金额合计
        final List<Integer> purchaseYearSumCell = getStockGiftRow(sheet, PURCHASE_YEAR_SUM);
        if (purchaseYearSumCell.isEmpty()) {
            throw new BusinessException("模板无法匹配！purchase_year_sum注释不存在");
        }
        sheet.getCells().get(purchaseYearSumCell.get(0), purchaseYearSumCell.get(1)).putValue(100000.00);

        // 当前库存剩余价值
        final List<Integer> purchaseYearValue = getStockGiftRow(sheet, PURCHASE_YEAR_VALUE);
        if (purchaseYearValue.isEmpty()) {
            throw new BusinessException("模板无法匹配！purchase_year_value注释不存在");
        }
        sheet.getCells().get(purchaseYearValue.get(0), purchaseYearValue.get(1)).putValue(20000.00);

        String path = stockWeekFilePath + System.currentTimeMillis() + ".xlsx";
        workbook.save(path);
        return path;
    }

    public static final String PURCHASE_MONTH_GIFT = "purchase_month_gift";
    public static final String PURCHASE_MONTH_SUM = "purchase_month_sum";
    public static final String PURCHASE_YEAR_DATE = "purchase_year_date";
    public static final String PURCHASE_YEAR_SUM = "purchase_year_sum";
    public static final String PURCHASE_YEAR_VALUE = "purchase_year_value";

    /**
     * 返回注解所在单元格的row, column
     * @param sheet
     * @param flag
     * @return
     */
    private List<Integer> getStockGiftRow(Worksheet sheet, String flag) {
        CommentCollection comments = sheet.getComments();
        for (int i = 0; i < comments.getCount(); i++) {
            Comment comment = comments.get(i);
            //note: Administrator:\nxian_gift
            final String note = comment.getNote();
            if (note.contains(PURCHASE_MONTH_GIFT) && PURCHASE_MONTH_GIFT.equals(flag)) {
                return List.of(comment.getRow(), comment.getColumn());
            } else if (note.contains(PURCHASE_MONTH_SUM) && PURCHASE_MONTH_SUM.equals(flag)) {
                return List.of(comment.getRow(), comment.getColumn());
            } else if (note.contains(PURCHASE_YEAR_DATE) && PURCHASE_YEAR_DATE.equals(flag)) {
                return List.of(comment.getRow(), comment.getColumn());
            } else if (note.contains(PURCHASE_YEAR_SUM) && PURCHASE_YEAR_SUM.equals(flag)) {
                return List.of(comment.getRow(), comment.getColumn());
            } else if (note.contains(PURCHASE_YEAR_VALUE) && PURCHASE_YEAR_VALUE.equals(flag)) {
                return List.of(comment.getRow(), comment.getColumn());
            }
        }
        return List.of();
    }

    /**
     * 生成采购表格
     * @param headerRow 标题行
     * @param list 数据
     * @param sheet sheet
     */
    private void createPurchaseTable(int headerRow, List<PurchaseSummaryAmount> list, Worksheet sheet, Style dataStyle) {
        Cells cells = sheet.getCells();
        int dataRow = headerRow + 1;
        if (list.isEmpty()) {
            createEmpty("本周无采购", dataRow, cells, sheet.getWorkbook());
        } else {
            for (int r = 0; r < list.size(); r++, dataRow++) {
                cells.insertRow(dataRow);
                PurchaseSummaryAmount ps = list.get(r);
                cells.get(dataRow, 0).putValue(ps.getName());
                cells.get(dataRow, 0).setStyle(dataStyle);
                cells.get(dataRow, 1).putValue(ps.getQuantity());
                cells.get(dataRow, 1).setStyle(dataStyle);
                cells.get(dataRow, 2).putValue(ps.getUnit());
                cells.get(dataRow, 2).setStyle(dataStyle);
                cells.get(dataRow, 3).putValue(ps.getPrice().setScale(2, RoundingMode.FLOOR).toString());
                cells.get(dataRow, 3).setStyle(dataStyle);
                //总价合并
                cells.merge(dataRow, 4, 1, 2);
                cells.get(dataRow, 4).putValue(ps.getTotalAmount().setScale(2, RoundingMode.FLOOR).toString());
                cells.get(dataRow, 4).setStyle(dataStyle);
                cells.get(dataRow, 5).setStyle(dataStyle);
                cells.getRows().get(dataRow).setHeight(35);
            }
        }
    }

    private Style dataStyle(Workbook workbook) {
        Style style = createCommonStyle(workbook);
        style.getFont().setSize(10);
        style.getFont().setColor(Color.getBlack());
        return style;
    }

    private void createEmpty(String emptyStr, int row, Cells cells, Workbook workbook) {
        cells.insertRow(row);
        cells.get(row, 0).putValue(emptyStr);
        cells.getRows().get(row).setHeight(30);
        //样式
        Style style = createCommonStyle(workbook);
        // 设置字体
        style.getFont().setName("SimSun");
        style.getFont().setSize(10);
        style.getFont().setColor(Color.getDarkGray());
        cells.get(row, 0).setStyle(style);
        cells.get(row, 1).setStyle(style);
        cells.get(row, 2).setStyle(style);
        cells.get(row, 3).setStyle(style);
        cells.get(row, 4).setStyle(style);
        cells.get(row, 5).setStyle(style);
        cells.merge(row, 0, 1, 6);
    }

    private Style createCommonStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.getFont().setName("SimSun");
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.setTextWrapped(true);
        return style;
    }

    /**
     * 出库价值合计行
     */
    public void insertSumValueRow(int rowNum, BigDecimal value, Worksheet worksheet) {
        Cells  cells = worksheet.getCells();
        cells.insertRow(rowNum);
        cells.get(rowNum, 0).putValue(String.format("本周出库价值合计：%.2f元", value));
        //样式
        cells.getRows().get(rowNum).setHeight(35);
        Style style = worksheet.getWorkbook().createStyle();
        style.getFont().setName("SimSun");
        style.getFont().setSize(10);
        style.getFont().setBold(true);
        style.getFont().setColor(Color.getBlack());
        style.setHorizontalAlignment(TextAlignmentType.LEFT);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.setTextWrapped(true);
        cells.get(rowNum, 0).setStyle(style);
        cells.get(rowNum, 1).setStyle(style);
        cells.get(rowNum, 2).setStyle(style);
        cells.get(rowNum, 3).setStyle(style);
        cells.get(rowNum, 4).setStyle(style);
        cells.get(rowNum, 5).setStyle(style);
        //合并
        cells.merge(rowNum, 0, 1, 6);

    }

    /**
     * 库房出入库标题样式
     * @param workbook
     * @return
     */
    private Style createStockTableHeaderStyle(Workbook workbook) {
        Style style = this.createCommonStyle(workbook);
        style.getFont().setName("SimSun");
        style.getFont().setBold(true);
        style.getFont().setSize(11);
        style.getFont().setColor(Color.getBlack());
        return style;
    }

    private Style createWarehouseHeaderStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.getFont().setName("SimSun");
        style.getFont().setBold(true);
        style.getFont().setSize(14);
        style.getFont().setColor(Color.getBlack());
        
        return style;
    }

    /**
     * 生成礼品表格
     * @param headerRow 标题行
     * @param list 数据
     * @param worksheet worksheet
     * @return 最后一行行号(0-based)
     */
    private int createStockTable(String warehouse, int headerRow, List<Stock> list, Worksheet worksheet, Style style) {
        int curRow = headerRow;
        Cells cells = worksheet.getCells();
        //库房标题
        Style headerStyle = createWarehouseHeaderStyle(worksheet.getWorkbook());
        cells.insertRow(headerRow);
        cells.get(curRow, 0).putValue(warehouse);
        cells.get(curRow, 0).setStyle(headerStyle);
        cells.getRows().get(curRow).setHeight(22);
        //表格标题行
        curRow++;
        Style tableHeader = createStockTableHeaderStyle(worksheet.getWorkbook());
        cells.insertRow(curRow);
        cells.get(curRow, 0).putValue("礼品");
        cells.get(curRow, 0).setStyle(tableHeader);
        cells.get(curRow, 1).putValue("出入数量");
        cells.get(curRow, 1).setStyle(tableHeader);
        cells.get(curRow, 2).putValue("使用人");
        cells.get(curRow, 2).setStyle(tableHeader);
        cells.get(curRow, 3).putValue("用途");
        cells.get(curRow, 3).setStyle(tableHeader);
        cells.get(curRow, 4).putValue("使用日期");
        cells.get(curRow, 4).setStyle(tableHeader);
        cells.get(curRow, 5).putValue("库存");
        cells.get(curRow, 5).setStyle(tableHeader);
        cells.getRows().get(curRow).setHeight(15);
        curRow++;
        //处理数据
        List<String> mids = list.stream().map(Stock::getMaterialId).toList();
        final List<MaterialDetail> mds = materialDetailRepository.findByIdIn(mids);
        final Map<String, MaterialDetail> mtMap = mds.stream().collect(Collectors.toMap(MaterialDetail::getId, md -> md));
        list = formatExcelData(list, mtMap);
        int dataRow = curRow;
        final Map<String, List<Stock>> map = list.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getName() + "|" + item.getSpecification() + "|" + item.getUnit(), // 组合键
                        TreeMap::new,
                        Collectors.toList()));
        if (list.isEmpty()) {
            createEmpty("本周无出入库", dataRow, cells, worksheet.getWorkbook());
        } else {
            int start = dataRow;
            Style redStyle = dataStyle(worksheet.getWorkbook());
            redStyle.getFont().setBold(true);
            redStyle.getFont().setColor(Color.getRed());
            Style greenStyle = dataStyle(worksheet.getWorkbook());
            greenStyle.getFont().setBold(true);
            greenStyle.getFont().setColor(Color.getGreen());
            //出库合计
            BigDecimal valueSum = BigDecimal.ZERO;
            for(Map.Entry<String, List<Stock>> entry : map.entrySet()) {
                List<Stock> vlist = entry.getValue();
                //按日期排序
                vlist.sort(Comparator.comparing(Stock::getOrderDate));
                for (int r = 0; r < vlist.size(); r++) {
                    Stock stock = vlist.get(r);
                    dataRow = start + r;
                    insertStockRow(stock, dataRow, cells, style, redStyle, greenStyle);
                    valueSum = valueSum.add(stock.getTotalPrice().setScale(2, RoundingMode.HALF_UP));
                    //行高35
                    worksheet.getCells().getRows().get(dataRow).setHeight(35);
                }
                //合并单元格
                if (vlist.size() > 1) {
                    int mergedRow = vlist.size();
                    //名称，第一列
                    cells.merge(start, 0, mergedRow, 1);
                    //库存，第六列
                    cells.merge(start, 5, mergedRow, 1);
                }
                start = start + vlist.size();
            }
            //总价合计
            insertSumValueRow(start, valueSum, worksheet);
        }
        return dataRow;
    }

    private void insertStockRow(Stock stock, int curRowIdx, Cells cells, Style style, Style redStyle, Style greenStyle) {
        cells.insertRow(curRowIdx);
        cells.get(curRowIdx, 0).putValue(stock.getName());
        cells.get(curRowIdx, 0).setStyle(style);
        cells.get(curRowIdx, 1).putValue(stock.getQuantityStr());
        if (STOCK_TYPE_IN == stock.getStockType()) {
            cells.get(curRowIdx, 1).setStyle(greenStyle);
        } else if (STOCK_TYPE_OUT == stock.getStockType()) {
            cells.get(curRowIdx, 1).setStyle(redStyle);
        } else {
            cells.get(curRowIdx, 1).setStyle(style);
        }
        cells.get(curRowIdx, 2).putValue(stock.getUsername());
        cells.get(curRowIdx, 2).setStyle(style);
        cells.get(curRowIdx, 3).putValue(stock.getUsage());
        cells.get(curRowIdx, 3).setStyle(style);
        cells.get(curRowIdx, 4).putValue(stock.getOrderDateStr());
        cells.get(curRowIdx, 4).setStyle(style);
        cells.get(curRowIdx, 5).putValue(stock.getInventoryStr());
        cells.get(curRowIdx, 5).setStyle(style);
    }

    private BigDecimal sumPurchases(List<PurchaseSummaryAmount> list) {
        return list.stream().map(PurchaseSummaryAmount::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CellRangeAddress> mergeData(List<Stock> list, int startRowIdx, int colIdx) {
        List<CellRangeAddress> mergedCells = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return mergedCells;
        }
        final LinkedHashMap<String, List<Stock>> map = list.stream().collect(Collectors.groupingBy(Stock::getMaterialId, LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<String, List<Stock>> entry : map.entrySet()) {
            List<Stock> v = entry.getValue();
            v.sort(Comparator.comparing(Stock::getOrderDate));
            if (!v.isEmpty() && v.size() > 1) {
                int mergedRows = v.size();
                CellRangeAddress cra = new CellRangeAddress(startRowIdx, startRowIdx + mergedRows - 1, colIdx, colIdx);
                mergedCells.add(cra);
            }
            startRowIdx = startRowIdx + v.size();

        }
        return mergedCells;
    }

    public List<Stock> formatExcelData(List<Stock> list, Map<String, MaterialDetail> materialDetailMap) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        for (Stock stock : list) {
            //单价
            BigDecimal price = materialDetailMap.getOrDefault(stock.getMaterialId(), new MaterialDetail()).getPrice();
            stock.setPrice(price);
            //名称
            String name = stock.getMaterialName();
            if (StringUtils.isNotBlank(stock.getSpecification())) {
                String spec = String.format("(%s)", stock.getSpecification());
                name = String.format("%s\n%s", stock.getMaterialName(), spec);
            }
            //单价
            if (price != null) {
                name = String.format("%s/%.2f元", name, price);
            }
            stock.setName(name);
            //使用人
            if (StringUtils.isNotBlank(stock.getDeptName())) {
                stock.setUsername(String.format("%s\n(%s)", stock.getUsername(), stock.getDeptName()));
            }
            //用途
            String bizType = "";
            if (StringUtils.isNotBlank(stock.getBizType())) {
                bizType = String.format("(%s)", stock.getBizType());
            }
            final String str = Stream.of(stock.getUsage(), stock.getRemark(), stock.getOrderRemark()).filter(StringUtils::isNotBlank).collect(Collectors.joining(";"));
            stock.setUsage(bizType + str);
            //数量
            final int stockType = stock.getStockType();
            switch (stockType) {
                case STOCK_TYPE_IN -> stock.setQuantityStr("+" + formatDouble(stock.getNum()) + stock.getUnit());
                case STOCK_TYPE_OUT -> stock.setQuantityStr("-" + formatDouble(stock.getNum()) + stock.getUnit());
                default -> stock.setQuantityStr(stock.getNum() + stock.getUnit());
            }
            //出库价值
            if (price != null) {
                BigDecimal value = price.multiply(BigDecimal.valueOf(stock.getNum())).setScale(2, RoundingMode.HALF_UP);
                if(STOCK_TYPE_IN == stockType) {
                    value = value.negate();
                }
                stock.setTotalPrice(value);
            } else {
                stock.setTotalPrice(BigDecimal.ZERO);
            }
            //日期
            if (stock.getOrderDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月dd日");
                stock.setOrderDateStr(stock.getOrderDate().format(formatter));
            }
            //库存
            if (stock.getInventory() != null) {
                stock.setInventoryStr(formatDouble(stock.getInventory()) + stock.getUnit());
            }
        }
        return list;
    }

    private static String formatDouble(Double value) {
        // 检查是否为整数（小数部分为 0）
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.format("%.0f", value); // 格式化为整数
        } else {
            return String.valueOf(value); // 保留原有小数位
        }
    }


    /**
     * 礼品使用情况
     * @param year    指定年份
     * @param monthIn 包含月份
     * @return List<MonthlyStockStatsDTO>
     */
    public List<MonthlyStockStatsDTO> createGiftUseMonthlyData(int year, List<Integer> monthIn) {
        return stockRepository.findGiftMonthlyDataBy(year, monthIn, StockType.OUT.getValue());
    }


    @Override
    public void createGiftInventoryAndValueExcel(OutputStream outputStream, int year1, int year2, List<Integer> monthIn) throws Exception {
        //对比数据
        final List<MonthlyStockStatsDTO> data1 = createGiftUseMonthlyData(year1, monthIn);
        final List<MonthlyStockStatsDTO> data2 = createGiftUseMonthlyData(year2, monthIn);

        //库存数据
        final List<MaterialType> giftTypes = materialTypeRepository.findByNameContaining(GIFT_TYPE);
        InventoryRequestForm form = new InventoryRequestForm();
        final List<String> typeList = giftTypes.stream().map(MaterialType::getId).toList();
        form.setMaterialTypeIds(typeList);
        form.setLimit(9999);
        final List<Inventory> inventoryList = this.latestInventories(form).getContent();
        WithQueryUtil.build(inventoryList);

        InventoryExcel ie = new InventoryExcel(inventoryList, data1, data2, year1, year2, monthIn);
        ie.createInventoryExportExcel(outputStream);
    }

    @Override
    public Map<String, Warehouse> findGiftWarehouseMap(Boolean enabled) {
        final List<Warehouse> list = warehouseRepository.findGiftWarehouse(true);
        return list.stream().collect(Collectors.toMap(Warehouse::getId, i -> i));
    }


    /**
     * 查询所有物品及库存信息（含预警）
     */
    public void findAllMaterialAndInventory() {

    }


}
