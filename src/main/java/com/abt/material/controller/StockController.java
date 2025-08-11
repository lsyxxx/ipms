package com.abt.material.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.JsonUtil;
import com.abt.material.entity.*;
import com.abt.material.model.*;
import com.abt.material.service.StockService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.service.IFileService;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.util.MapUtils;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;
import com.abt.wf.model.PurchaseSummaryAmount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 出入库
 */
@RestController
@Slf4j
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;
    private final IFileService iFileService;

    @Value("${abt.stock.inv.export.template}")
    private String templatePath;

    @Value("${abt.stock.inv.alert.export.template}")
    private String alertTemplatePath;

    @Value("${abt.stock.inv.alert.export.template2}")
    private String alertTemplatePath2;

    @Value("${com.abt.file.upload.save}")
    private String savedRoot;


    public StockController(StockService stockService, IFileService iFileService) {
        this.stockService = stockService;
        this.iFileService = iFileService;
    }


    /**
     * 出入库登记
     */
    @PostMapping("/reg")
    public R<Object> stockInOrder(@RequestBody @Validated({ValidateGroup.Save.class}) StockOrder order) {
        stockService.saveStockOrder(order);
        return R.success("登记成功!");
    }

    /**
     * 出入库单明细
     */
    @GetMapping("/dtl/one")
    public R<StockOrder> stockInOrderDetail(String orderId) {
        final StockOrder detail = stockService.findDetailsByOrderId(orderId);
        return R.success(detail);
    }

    /**
     * 出入库产品明细表
     *
     * @param requestForm 查询条件
     */
    @PostMapping("/dtl/table")
    public R<List<Stock>> findStockList(@RequestBody StockOrderRequestForm requestForm) {
        final Page<Stock> page = stockService.findStocksByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/order/list")
    public R<List<StockOrder>> findStockOrderList(StockOrderRequestForm requestForm) {
        final Page<StockOrder> page = stockService.findStockOrderPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }


    /**
     * 添加/编辑仓库信息
     */
    @PostMapping("/wh/save")
    public R<Object> saveWarehouse(@RequestBody @Validated({ValidateGroup.Save.class}) Warehouse warehouse) {
        stockService.saveWarehouse(warehouse);
        return R.success("保存成功!");
    }

    @GetMapping("/wh/find/allBy")
    public R<List<Warehouse>> findAllWarehouse(WarehouseRequestForm requestForm) {
        final List<Warehouse> list = stockService.findAllWarehouseBy(requestForm);
        return R.success(list);
    }

    @GetMapping("/wh/newNo")
    public R<Long> getWarehouseSortNo() {
        final long count = stockService.getWarehouseSortNoForNew();
        return R.success(count);

    }

    @GetMapping("/wh/del")
    public R<Object> deleteWarehouse(String id) {
        stockService.deleteWarehouse(id);
        return R.success("删除仓库成功!");
    }

    @GetMapping("/wh/giftMap")
    public R<Object> findGiftWarehouse(Boolean enabled) {
        final Map<String, Warehouse> map = stockService.findGiftWarehouseMap(enabled);
        return R.success(map, "获取礼品仓库信息成功");
    }


    @PostMapping("/inv/latestList")
    public R<List<Inventory>> latestInventoryList(@RequestBody InventoryRequestForm requestForm) {
        final Page<Inventory> page = stockService.latestInventories(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    /**
     * 导出预警库存
     */
    @GetMapping("/inv/alert/export")
    public void exportInventoryAlert(HttpServletResponse response,
                                     @RequestParam(required = false, defaultValue = "") List<String> warehouseIds,
                                     @RequestParam(required = false, defaultValue = "") List<String> materialTypeIds) throws IOException {

        InventoryRequestForm inventoryRequestForm = new InventoryRequestForm();
        inventoryRequestForm.setWarehouseIds(warehouseIds);
        inventoryRequestForm.setMaterialTypeIds(materialTypeIds);
        String fileName = URLEncoder.encode("inv_alert_export.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        final List<InventoryAlert> list = stockService.findInventoryAlertList(inventoryRequestForm);
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), InventoryAlert.class).autoCloseStream(Boolean.FALSE)
                .withTemplate(templatePath).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(new FillWrapper("m", list), fillConfig, writeSheet);
            //sheet2:库房
            final List<Warehouse> whs = stockService.findAllWarehouseBy(new WarehouseRequestForm());
            WriteSheet sheet2 = EasyExcel.writerSheet(1).build();
            excelWriter.fill(new FillWrapper("w", whs), sheet2);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            final R<Object> fail = R.fail("预警库存导出失败!!");
            response.getWriter().println(JsonUtil.toJson(fail));
        }
    }

    /**
     * 导出全部物品资料
     */
    @GetMapping("/dtl/invExport")
    public void exportStockDetailAll(HttpServletResponse response,
                                     @RequestParam(required = false, defaultValue = "") List<String> warehouseIds,
                                     @RequestParam(required = false, defaultValue = "") List<String> materialTypeIds) throws IOException {
        InventoryRequestForm inventoryRequestForm = new InventoryRequestForm();
        inventoryRequestForm.setWarehouseIds(warehouseIds);
        inventoryRequestForm.setMaterialTypeIds(materialTypeIds);
        final List<MaterialDetailDTO> list = stockService.findAllMaterialInventories(inventoryRequestForm);
        String fileName = URLEncoder.encode("inv_export.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        final List<Warehouse> whs = stockService.findAllWarehouseBy(new WarehouseRequestForm());
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), MaterialDetailDTO.class).autoCloseStream(Boolean.TRUE)
                .withTemplate(templatePath).build()) {
            //sheet1
            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(new FillWrapper("m", list), fillConfig, writeSheet);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("exportDate", LocalDate.now());
            excelWriter.fill(map, writeSheet);
            //sheet2:库房
            WriteSheet sheet2 = EasyExcel.writerSheet(1).build();
            excelWriter.fill(new FillWrapper("w", whs), sheet2);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            final R<Object> fail = R.fail("物品库存数据导出失败!");
            response.getWriter().println(JsonUtil.toJson(fail));
        }
    }

    public static final String SESSION_CHECK_BILL = "stockCheckBillOrder";

    @PostMapping("/chk/import")
    public R<List<MaterialDetailDTO>> importCheckBill(MultipartFile file, String orderJson, HttpServletRequest request) throws JsonProcessingException {
        SystemFile systemFile = iFileService.saveFile(file, savedRoot, "stockCheckBill", true, true);
        StockOrder order = JsonUtil.toObject(orderJson, new TypeReference<StockOrder>() {
        });
        File f = new File(systemFile.getFullPath());
        order.setFileList(JsonUtil.toJson(systemFile));
        order = stockService.importCheckBill(f, order);
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_CHECK_BILL, order);
        if (order.isHasError()) {
            return R.fail(order.getErrorList(), "导入数据失败!");
        } else {
            return R.success(order.getMaterialDetailDTOList());
        }
    }

    @GetMapping("/chk/save")
    public R<Object> saveCheckBill(HttpServletRequest request) {
        final HttpSession session = request.getSession();
        if (session == null) {
            throw new BusinessException("页面会话超时，请刷新后重新导入盘点数据");
        }
        final Object attribute = session.getAttribute(SESSION_CHECK_BILL);
        if (attribute == null) {
            throw new BusinessException("无导入的盘点数据，请刷新后重新导入");
        }
        StockOrder order = (StockOrder) attribute;
        if (order.isHasError()) {
            throw new BusinessException("导入的盘点数据有错误，请重新导入后保存");
        }
        stockService.saveStockOrder(order);
        return R.success("保存盘点单成功!");
    }


    @GetMapping("/type/find")
    public R<List<MaterialType>> findAllMaterialType(MaterialTypeRequestForm requestForm) {
        if (requestForm.getIds() == null || requestForm.getIds().isEmpty()) {
            requestForm.setIds(List.of("all"));
        }
        final List<MaterialType> all = stockService.findAllMaterialType(requestForm);
        return R.success(all);
    }

    @GetMapping("/chk/del")
    public R<Object> deleteCheckBill(String id) {
        stockService.hardDeleteCheckBill(id);
        return R.success("已删除盘点单及库存信息");
    }

    /**
     * 导入库存预警数据
     */
    @PostMapping("/inv/alert/import")
    public R<List<InventoryAlert>> importInventoryAlert(MultipartFile file, HttpServletRequest request) {
        SystemFile systemFile = iFileService.saveFile(file, savedRoot, "stockInvAlert", true, true);
        File f = new File(systemFile.getFullPath());
        final List<InventoryAlert> errorList = stockService.importInventoryAlertFile(f);
        if (!errorList.isEmpty()) {
            return R.fail(errorList, "导入失败! 请修改错误后重新上传!");
        }
        return R.success("导入成功");
    }

    /**
     * 下载库存预警模板
     */
    @GetMapping("/inv/alert/download")
    public void downloadInventoryAlertTemplate(HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("inventory_alert_template.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        final List<Warehouse> whs = stockService.findAllWarehouseBy(new WarehouseRequestForm());
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), Warehouse.class).autoCloseStream(Boolean.FALSE)
                .withTemplate(alertTemplatePath2).build()) {
            //sheet2:库房
            WriteSheet sheet2 = EasyExcel.writerSheet(1).build();
            excelWriter.fill(new FillWrapper("w", whs), sheet2);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            final R<Object> fail = R.fail("下载库存预警模板失败!");
            response.getWriter().println(JsonUtil.toJson(fail));
        }

    }

    @GetMapping("/inv/alert/get")
    public R<List<InventoryAlert>> getInventoryAlert(String mid) {
        final List<InventoryAlert> list = stockService.getInventoryAlert(mid);
        return R.success(list);
    }

    @PostMapping("/inv/alert/save")
    public R<Object> saveInventoryAlert(@RequestBody ArrayList<InventoryAlert> list) {
        stockService.saveInventoryAlertList(list);
        return R.success("保存成功!");
    }


    /**
     * 周报汇总
     */
    @GetMapping("/summary/week")
    public R<StockSummaryTable> weekSummary(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        if (startDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        if (endDate == null) {
            LocalDate now = LocalDate.now();
            endDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }
        StockSummaryTable table = new StockSummaryTable();
        final List<PurchaseSummaryAmount> list = stockService.summaryPurchaseGiftTotalAmount(startDate, endDate);
        table.setPurchaseSummaryAmountList(list);
        stockService.inventoryGiftDetails(startDate, endDate, table);
        return R.success(table);
    }

    /**
     * 月报汇总
     *
     * @param yearMonth yyyy-mm 年月
     */
    @GetMapping("/summary/month")
    public R<StockSummaryTable> monthSummary(String yearMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        if (StringUtils.isBlank(yearMonth)) {
            yearMonth = LocalDate.now().format(formatter);
        }
        YearMonth ym = YearMonth.parse(yearMonth, formatter);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        StockSummaryTable table = new StockSummaryTable();
        //月
        final List<PurchaseSummaryAmount> list = stockService.summaryPurchaseGiftTotalAmount(startDate, endDate);
        table.setPurchaseSummaryAmountList(list);
        //年
        final List<PurchaseSummaryAmount> yearSummary = stockService.summaryPurchaseGiftTotalAmount(LocalDate.of(ym.getYear(), 1, 1), endDate);
        table.setYearSummary(yearSummary);
        stockService.stockSummary(startDate, endDate, table);
        return R.success(table);
    }


    @GetMapping("/export/week")
    public R<String> exportWeekSummary(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws Exception {
        if (startDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        if (endDate == null) {
            LocalDate now = LocalDate.now();
            endDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }

        StockSummaryTable table = new StockSummaryTable();
        final List<PurchaseSummaryAmount> list = stockService.summaryPurchaseGiftTotalAmount(startDate, endDate);
        table.setPurchaseSummaryAmountList(list);
        stockService.inventoryGiftDetails(startDate, endDate, table);
        final Map<String, Warehouse> giftWarehouseMap = stockService.findGiftWarehouseMap(true);
        final String path = stockService.createExcelWeek(table, startDate, endDate, giftWarehouseMap);
        return R.success(path, "导出成功!");
    }

    /**
     * 导出库存对比分析及库存价值
     */
    @PostMapping("/inv/smry/download")
    public void downloadInventoryAndValueExcel(HttpServletResponse response,
                                               @RequestBody InventoryRequestForm requestForm) throws Exception {
        try {
            stockService.createGiftInventoryAndValueExcel(response.getOutputStream(), requestForm.getYear1(), requestForm.getYear2(), requestForm.getMonthIn());
            //下载excel

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");

            // 设置文件名
            String fileName = "礼品使用分析.xlsx";
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            final R<Object> fail = R.fail("下载礼品使用分析Excel失败!");
            response.getWriter().println(JsonUtil.toJson(fail));
        }
    }


}
