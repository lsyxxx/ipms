package com.abt.material.service;

import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import com.abt.material.entity.Warehouse;
import com.abt.material.model.StockOrderRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockService {
    /**
     * 出入库单
     * @param stockOrder 出入库单详情
     */
    void saveStockOrder(StockOrder stockOrder);

    StockOrder findWithStockListByOrderId(String orderId);

    /**
     * 出入库产品明细
     * @param requestForm 查询条件
     */
    Page<Stock> findStocksByQueryPageable(StockOrderRequestForm requestForm);

    void saveWarehouse(Warehouse warehouse);

    List<Warehouse> findAllWarehouse();
}
