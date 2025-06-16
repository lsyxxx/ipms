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


    public StockServiceImpl(StockOrderRepository stockOrderRepository, StockRepository stockRepository, WarehouseRepository warehouseRepository, MaterialDetailRepository materialDetailRepository, InventoryRepository inventoryRepository, MaterialTypeRepository materialTypeRepository, InventoryAlertRepository inventoryAlertRepository, PurchaseApplyDetailRepository purchaseApplyDetailRepository) {
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

    //延安库房
    public static final String WH_GIFT_YANAN = "c04c456a-7cc2-475a-8449-b8a3c18d7784";
    //西安库房
    public static final String WH_GIFT_XIAN = "44746bb0-30dd-48ad-a57d-de1e160bf1d4";
    //成都库房
    public static final String WH_GIFT_CHENGDU = "fe4ef039-65fb-4d94-858a-d8683971ba53";

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

        Comparator<Stock> comparator = Comparator.comparing(Stock::getMaterialName)
                .thenComparing(Stock::getSpecification)
                .thenComparing(Stock::getUnit)
                .thenComparing(Stock::getOrderDate);

        //库房
        final List<Stock> yanan = stockList.stream().filter(i -> WH_GIFT_YANAN.equals(i.getWarehouseId())).sorted(comparator).toList();
        final List<Stock> xian = stockList.stream().filter(i -> WH_GIFT_XIAN.equals(i.getWarehouseId())).sorted(comparator).toList();
        final List<Stock> chengdu = stockList.stream().filter(i -> WH_GIFT_CHENGDU.equals(i.getWarehouseId())).sorted(comparator).toList();

        table.setXianStockDetails(xian);
        table.setYananStockDetails(yanan);
        table.setChengduStockDetails(chengdu);
        return table;
    }

    @Override
    public void stockSummary(LocalDate startDate, LocalDate endDate, StockSummaryTable table) {
        final List<StockQuantitySummary> stockIn = stockRepository.summaryGiftQuantity(STOCK_TYPE_IN, startDate, endDate);
        final List<StockQuantitySummary> stockOut = stockRepository.summaryGiftQuantity(STOCK_TYPE_OUT, startDate, endDate);
        table.setStockInSummary(stockIn);
        table.setStockOutSummary(stockOut);
    }

    @Override
    public String createExcelWeek(StockSummaryTable summaryTable, LocalDate startDate, LocalDate endDate) throws Exception {
        Workbook workbook = new Workbook(stockWeekTemplate);
        Worksheet sheet = workbook.getWorksheets().get(0);
        //日期行
        sheet.getCells().get(1, 0).putValue(TimeUtil.toYYYY_MM_DDString(startDate) + "至" + TimeUtil.toYYYY_MM_DDString(endDate));
        Style dataStyle = dataStyle(workbook);
        //西安礼品表
        createStockTable(getStockGiftRow(sheet, XIAN_COMMENT),
                summaryTable.getXianStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList(),
                sheet, dataStyle);
        //延安
        createStockTable(getStockGiftRow(sheet, YANAN_COMMENT),
                summaryTable.getYananStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList(),
                sheet, dataStyle);
        //成都
        createStockTable(getStockGiftRow(sheet, CHENGDU_COMMENT),
                summaryTable.getChengduStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList(),
                sheet, dataStyle);
        //采购
        int purRow = getStockGiftRow(sheet, PURCHASE_COMMENT);
        createPurchaseTable(purRow, summaryTable.getPurchaseSummaryAmountList(), sheet, dataStyle);
        //合计
        int sumRow = getStockGiftRow(sheet, PURCHASE_SUM_COMMENT);
        final BigDecimal purSum = sumPurchases(summaryTable.getPurchaseSummaryAmountList());
        sheet.getCells().get(sumRow, 4).putValue(purSum.setScale(2, RoundingMode.FLOOR).toString());
        String path = stockWeekFilePath + System.currentTimeMillis() + ".xlsx";
        workbook.save(path);
        return path;
    }

    public static final String XIAN_COMMENT = "xian_gift";
    public static final String YANAN_COMMENT = "yanan_gift";
    public static final String CHENGDU_COMMENT = "chengdu_gift";
    public static final String PURCHASE_COMMENT = "purchase_gift";
    public static final String PURCHASE_SUM_COMMENT = "purchase_sum";

    private int getStockGiftRow(Worksheet sheet, String flag) {
        CommentCollection comments = sheet.getComments();
        for (int i = 0; i < comments.getCount(); i++) {
            Comment comment = comments.get(i);
            //note: Administrator:\nxian_gift
            final String note = comment.getNote();
            if (note.contains(XIAN_COMMENT) && XIAN_COMMENT.equals(flag)) {
                //西安礼品表
                return comment.getRow();
            } else if (note.contains(YANAN_COMMENT) && YANAN_COMMENT.equals(flag)) {
                return comment.getRow();
            } else if (note.contains(CHENGDU_COMMENT) && CHENGDU_COMMENT.equals(flag)) {
                return comment.getRow();
            } else if (note.contains(PURCHASE_COMMENT) && PURCHASE_COMMENT.equals(flag)) {
                return comment.getRow();
            } else if (note.contains(PURCHASE_SUM_COMMENT) && PURCHASE_SUM_COMMENT.equals(flag)) {
                return comment.getRow();
            }
        }
        return 0;
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
     * 生成礼品表格
     * @param headerRow 标题行
     * @param list 数据
     * @param worksheet worksheet
     */
    private void createStockTable(int headerRow, List<Stock> list, Worksheet worksheet, Style style) {
        //处理数据
        List<String> mids = list.stream().map(Stock::getMaterialId).toList();
        final List<MaterialDetail> mds = materialDetailRepository.findByIdIn(mids);
        final Map<String, MaterialDetail> mtMap = mds.stream().collect(Collectors.toMap(MaterialDetail::getId, md -> md));
        list = formatExcelData(list, mtMap);
        int dataRow = headerRow + 1;
        final Map<String, List<Stock>> map = list.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getName() + "|" + item.getSpecification() + "|" + item.getUnit(), // 组合键
                        TreeMap::new,
                        Collectors.toList()));
        Cells cells = worksheet.getCells();
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
                    valueSum = valueSum.add(stock.getTotalPrice().setScale(2, RoundingMode.FLOOR));
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
            insertSumValueRow(start, valueSum,  worksheet);
        }
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

    //使用easyexcel导出，合并有问题
//    @Override
//    public String createExcelWeek(StockSummaryTable summaryTable, LocalDate startDate, LocalDate endDate) throws IOException {
//        String path = stockWeekFilePath + System.currentTimeMillis() + ".xlsx";
//        int dataRow = 4;
//        List<CellRangeAddress> mergedRegions = new ArrayList<>();
//        //表格数据
//        final List<Stock> xianStockDetails = summaryTable.getXianStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList();
//        formatExcelData(xianStockDetails);
////        mergedRegions.addAll(mergeData(xianStockDetails, dataRow, NAME_COL));
////        mergedRegions.addAll(mergeData(xianStockDetails, dataRow, STOCK_COL));
//        final List<Stock> yananStockDetails = summaryTable.getYananStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList();
//        formatExcelData(yananStockDetails);
//        dataRow = dataRow + xianStockDetails.size() + 2;
////        mergedRegions.addAll(mergeData(yananStockDetails, dataRow, NAME_COL));
////        mergedRegions.addAll(mergeData(yananStockDetails, dataRow, STOCK_COL));
//        dataRow = dataRow + yananStockDetails.size() + 2;
//        final List<Stock> chengduStockDetails = summaryTable.getChengduStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList();
//        formatExcelData(chengduStockDetails);
////        mergedRegions.addAll(mergeData(chengduStockDetails, dataRow, NAME_COL));
////        mergedRegions.addAll(mergeData(chengduStockDetails, dataRow, STOCK_COL));
//        final List<PurchaseSummaryAmount> purchaseSummaryAmountList = summaryTable.getPurchaseSummaryAmountList();
//        purchaseSummaryAmountList.sort(
//                Comparator.comparing(PurchaseSummaryAmount::getName)
//                        .thenComparing(PurchaseSummaryAmount::getSpecification)
//                        .thenComparing(PurchaseSummaryAmount::getUnit)
//                        .thenComparing(PurchaseSummaryAmount::getPrice)
//        );
//        for (PurchaseSummaryAmount psa : purchaseSummaryAmountList) {
//            if (StringUtils.isNotBlank(psa.getSpecification())) {
//                psa.setName(String.format("%s(%s)", psa.getName(), psa.getSpecification()));
//            }
//        }
//        final BigDecimal sumPurchase = sumPurchases(purchaseSummaryAmountList);
//        int purchaseRow = 4;
//        purchaseRow = addRow(purchaseRow, xianStockDetails) + 2;
//        purchaseRow = addRow(purchaseRow, yananStockDetails) + 2;
//        purchaseRow = addRow(purchaseRow, chengduStockDetails) + 2;
//        if (!purchaseSummaryAmountList.isEmpty()) {
//            purchaseRow = purchaseRow + 1;
//        }
//        try (ExcelWriter writer = FastExcel.write(path).withTemplate(this.stockWeekTemplate)
//                .registerWriteHandler(new CellMergeHandler(purchaseRow))
//                .build()) {
//            WriteSheet writeSheet = FastExcel.writerSheet().build();
//            FillConfig fillConfig = FillConfig.builder().build();
//            fillConfig.setForceNewRow(true);
//            writer.fill(new FillWrapper("xian", xianStockDetails), fillConfig, writeSheet);
//            writer.fill(new FillWrapper("yanan", yananStockDetails), fillConfig, writeSheet);
//            writer.fill(new FillWrapper("chengdu", chengduStockDetails), fillConfig, writeSheet);
//            writer.fill(new FillWrapper("purchases", purchaseSummaryAmountList), fillConfig, writeSheet);
//            Map<String, Object> map = new HashMap<>();
//            map.put("startDate", TimeUtil.toYYYY_MM_DDString(startDate));
//            map.put("endDate", TimeUtil.toYYYY_MM_DDString(endDate));
//            map.put("purchaseAmount", sumPurchase.setScale(2, RoundingMode.FLOOR));
//            writer.fill(map, writeSheet);
//        }
//        return path;
//    }

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

    //使用aspose生成， 问题1：数据源（列表）没数据，不会清除模板中的占位符。 2. 合并问题
//    @Override
//    public String createExcelWeek(StockSummaryTable summaryTable, LocalDate startDate, LocalDate endDate) throws Exception {
//        Workbook workbook = new Workbook(stockWeekTemplate);
//        WorkbookDesigner designer = new WorkbookDesigner();
//        designer.setWorkbook(workbook); // 加载模板 Excel
//        //表格数据
//        List<Stock> xianStockDetails = summaryTable.getXianStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList();
//        xianStockDetails = formatExcelData(xianStockDetails);
//        List<Stock> yananStockDetails = summaryTable.getYananStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList();
//        yananStockDetails = formatExcelData(yananStockDetails);
//        List<Stock> chengduStockDetails = summaryTable.getChengduStockDetails().stream().filter(i -> !"采购".equals(i.getBizType())).toList();
//        chengduStockDetails = formatExcelData(chengduStockDetails);
//        List<PurchaseSummaryAmount> purchaseSummaryAmountList = summaryTable.getPurchaseSummaryAmountList();
//        for (PurchaseSummaryAmount psa : purchaseSummaryAmountList) {
//            if (StringUtils.isNotBlank(psa.getSpecification())) {
//                psa.setName(String.format("%s(%s)", psa.getName(), psa.getSpecification()));
//            }
//        }
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
//        designer.setDataSource("startDate", startDate.format(dateFormatter));
//        designer.setDataSource("endDate", endDate.format(dateFormatter));
//        designer.setDataSource("xian", xianStockDetails);
//        designer.setDataSource("yanan", yananStockDetails);
//        designer.setDataSource("chengdu", chengduStockDetails);
//        designer.setDataSource("purchases", purchaseSummaryAmountList);
//        designer.process(true);
//        String path = stockWeekFilePath + System.currentTimeMillis() + ".xlsx";
//        designer.getWorkbook().save(path);
//        return path;
//    }


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
                final BigDecimal value = price.multiply(BigDecimal.valueOf(stock.getNum())).setScale(2, RoundingMode.HALF_UP);
//                stock.setQuantityStr(stock.getQuantityStr() + "\n(" + value + "元)" );
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
        final List<MaterialType> giftTypes = materialTypeRepository.findByNameContaining("礼品类");
        InventoryRequestForm form = new InventoryRequestForm();
        final List<String> typeList = giftTypes.stream().map(MaterialType::getId).toList();
        form.setMaterialTypeIds(typeList);
        form.setLimit(9999);
        final List<Inventory> inventoryList = this.latestInventories(form).getContent();
        WithQueryUtil.build(inventoryList);

        InventoryExcel ie = new InventoryExcel(inventoryList, data1, data2, year1, year2, monthIn);
        ie.createInventoryExportExcel(outputStream);
    }


}
