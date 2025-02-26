package com.abt.material.listener;

import com.abt.material.entity.InventoryAlert;
import com.abt.material.entity.InventoryId;
import com.abt.material.entity.Warehouse;
import com.abt.material.service.StockService;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导入库存预警
 */
@Slf4j
public class ImportInventoryAlertListener implements ReadListener<InventoryAlert> {

    private StockService stockService;
    private List<InventoryAlert> saveList;
    private List<InventoryAlert> errorList;
    private Map<String, Warehouse> warehouseMap;

    public ImportInventoryAlertListener(List<InventoryAlert> saveList, StockService stockService, List<InventoryAlert> errorList, Map<String, Warehouse> warehouseMap) {
        this.saveList = saveList;
        this.stockService = stockService;
        this.errorList = errorList;
        this.warehouseMap = warehouseMap;
    }

    @Override
    public void invoke(InventoryAlert data, AnalysisContext context) {
        //数据校验
        stockService.checkInventoryAlertData(data, warehouseMap);
        //主键
        if (data.isError()) {
            this.errorList.add(data);
        } else {
            //保存
            String whName = data.getWarehouseName();
            final Warehouse warehouse = warehouseMap.get(whName);
            InventoryId id = new InventoryId(data.getMaterialId(), warehouse.getId());
            data.setId(id);
            saveList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("ImportInventoryAlertListener: Analysed all");
    }
}
