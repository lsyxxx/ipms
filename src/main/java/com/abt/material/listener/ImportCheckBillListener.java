package com.abt.material.listener;


import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import com.abt.material.model.MaterialDetailDTO;
import com.abt.material.service.StockService;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 导入盘点数据
 */
@Slf4j
public class ImportCheckBillListener implements ReadListener<MaterialDetailDTO> {

    private static final int BATCH_COUNT = 100;
    private StockOrder order;
    private StockService stockService;
    private List<MaterialDetailDTO> list;
    private List<MaterialDetailDTO> errorList;

    public ImportCheckBillListener(StockOrder order, StockService stockService, List<MaterialDetailDTO> list, List<MaterialDetailDTO> errorList) {
        this.order = order;
        this.stockService = stockService;
        this.list = list;
        this.errorList = errorList;
    }


    @Override
    public void invoke(MaterialDetailDTO data, AnalysisContext context) {
        log.info("ImportCheckBillListener invoked");
        //数据校验
        stockService.checkImportData(data);
        if (data.hasError()) {
            this.errorList.add(data);
        } else {
            //add
            list.add(data);
            stockService.addStock(order, data);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("ImportCheckBillListener: Analysed all");
    }


}
