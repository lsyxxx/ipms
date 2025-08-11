package com.abt.material.service;

import com.abt.material.entity.*;
import com.abt.material.model.*;
import com.abt.wf.model.PurchaseSummaryAmount;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StockService {
    /**
     * 出入库单
     *
     * @param stockOrder 出入库单详情
     */
    StockOrder saveStockOrder(StockOrder stockOrder);

    StockOrder findDetailsByOrderId(String orderId);

    /**
     * 出入库产品明细
     * @param requestForm 查询条件
     */
    Page<Stock> findStocksByQueryPageable(StockOrderRequestForm requestForm);

    void saveWarehouse(Warehouse warehouse);

    List<Warehouse> findAllWarehouseBy(WarehouseRequestForm requestForm);

    /**
     * 为新的仓库设置序号
     */
    Integer getWarehouseSortNoForNew();

    void deleteWarehouse(String id);

    Page<Inventory> latestInventories(InventoryRequestForm requestForm);

    /**
     * 查询所有物品的库存
     */
    List<MaterialDetailDTO> findAllMaterialInventories(InventoryRequestForm requestForm);

    /**
     * 导入盘点库存数据
     * 规则：
     * 1. 必须有物品id
     * 2. 必须有库房名称
     * 4. 导入的数据模板为导出的库存数据，从第4行开始读取数据
     * 5. 若存在重复的数据，则以最后一条为准
     * 6. 可多次上传，后面的会覆盖前面的
     *
     * @param file  导入的文件
     * @param order 盘点单
     */
    @Transactional
    StockOrder importCheckBill(File file, StockOrder order);

    void addStock(StockOrder order, MaterialDetailDTO dto);

    List<InventoryAlert> findInventoryAlertList(InventoryRequestForm requestForm);

    Page<InventoryAlert> findInventoryAlertPageable(InventoryRequestForm requestForm);

    Page<StockOrder> findStockOrderPageable(StockOrderRequestForm requestForm);

    List<MaterialType> findAllMaterialType(MaterialTypeRequestForm requestForm);

    @Transactional
    void hardDeleteCheckBill(String id);

    /**
     * 校验导入盘点数据，返回错误数据及错误信息
     */
    void checkImportData(MaterialDetailDTO row);

    void saveInventoryAlert(InventoryAlert inventoryAlert);

    /**
     * 库存预警设置
     */
    void saveInventoryAlertList(List<InventoryAlert> list);

    List<InventoryAlert> importInventoryAlertFile(File file);

    void checkInventoryAlertData(InventoryAlert row, Map<String, Warehouse> whMap);

    List<InventoryAlert> getInventoryAlert(String mid);

    /**
     * 礼品类合计采购金额，需审批通过，物品类型为“礼品类”
     * @param startDate 统计开始日期（根据
     * @param endDate 统计结束日期
     * @return list
     */
    List<PurchaseSummaryAmount> summaryPurchaseGiftTotalAmount(LocalDate startDate, LocalDate endDate);

    /**
     * 礼品类本周/本月的出入库明细
     */
    StockSummaryTable inventoryGiftDetails(LocalDate startDate, LocalDate endDate, StockSummaryTable table);

    /**
      * 汇总出入库情况
      * @param startDate 开始日期（包含）
      * @param endDate 结束日期（包含）
      */
    void stockSummary(LocalDate startDate, LocalDate endDate, StockSummaryTable table);

    /**
     * 导出礼品周报
     * @param summaryTable 礼品输入库数据表
     * @param startDate 数据开始日期
     * @param endDate 数据结束日期
     * @param warehouseMap 仓库
     * @return
     * @throws Exception
     */
    String createExcelWeek(StockSummaryTable summaryTable, LocalDate startDate, LocalDate endDate,
                           Map<String, Warehouse> warehouseMap) throws Exception;

    /**
     * 礼品类库存及价值分析excel
     */
    void createGiftInventoryAndValueExcel(OutputStream outputStream, int year1, int year2, List<Integer> monthIn) throws Exception;

    Map<String, Warehouse> findGiftWarehouseMap(Boolean enabled);
}
