package com.abt.material.service.impl;

import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import com.abt.material.entity.Warehouse;
import com.abt.material.model.StockOrderRequestForm;
import com.abt.material.repository.StockOrderRepository;
import com.abt.material.repository.StockRepository;
import com.abt.material.repository.WarehouseRepository;
import com.abt.material.service.StockService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 出入库
 */
@Service
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockOrderRepository stockOrderRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;

    public StockServiceImpl(StockOrderRepository stockOrderRepository, StockRepository stockRepository, WarehouseRepository warehouseRepository) {
        this.stockOrderRepository = stockOrderRepository;
        this.stockRepository = stockRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    @Override
    public void saveStockOrder(StockOrder stockOrder) {
        stockOrder = stockOrderRepository.save(stockOrder);
        List<Stock> list = new ArrayList<Stock>();
        if (stockOrder.getStockList() != null) {
            for (Stock stock : stockOrder.getStockList()) {
                stock.setOrderId(stockOrder.getId());
                list.add(stock);
            }
        }
        stockRepository.saveAllAndFlush(list);
    }


    @Override
    public StockOrder findWithStockListByOrderId(String orderId) {
        return stockOrderRepository.findWithStockListById(orderId).orElseThrow(() -> new BusinessException("未查询到出入库单据信息"));
    }

    @Override
    public Page<Stock> findStocksByQueryPageable(StockOrderRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getSize());
        final Page<Stock> page = stockRepository.findByQueryPageable(requestForm.getQuery(), requestForm.getStockType(), requestForm.getStartDate(), requestForm.getEndDate(), pageable);
        WithQueryUtil.build(page);
        return page;
    }


    @Override
    public void saveWarehouse(Warehouse warehouse) {
        warehouseRepository.save(warehouse);
    }

    @Override
    public List<Warehouse> findAllWarehouse() {
        return warehouseRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"));
    }




}
