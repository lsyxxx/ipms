package com.abt.material.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.material.entity.Inventory;
import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import com.abt.material.entity.Warehouse;
import com.abt.material.model.InventoryRequestForm;
import com.abt.material.model.StockOrderRequestForm;
import com.abt.material.model.WarehouseRequestForm;
import com.abt.material.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出入库
 */
@RestController
@Slf4j
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
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
     * @param id 单据id
     */
    @GetMapping("/dtl/one")
    public R<StockOrder> stockInOrderDetail(String id) {
        final StockOrder detail = stockService.findDetailsByOrderId(id);
        return R.success(detail);
    }

    /**
     * 出入库产品明细表
     * @param requestForm 查询条件
     */
    @GetMapping("/dtl/table")
    public R<List<Stock>> findStockList(StockOrderRequestForm requestForm) {
        final Page<Stock> page = stockService.findStocksByQueryPageable(requestForm);
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


    @PostMapping("/inv/latestList")
    public R<List<Inventory>> latestInventoryList(@RequestBody InventoryRequestForm requestForm) {
        final List<Inventory> list = stockService.latestInventories(requestForm);
        return R.success(list);
    }



}
