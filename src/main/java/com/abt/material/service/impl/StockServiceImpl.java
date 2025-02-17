package com.abt.material.service.impl;

import com.abt.material.entity.*;
import com.abt.material.model.InventoryRequestForm;
import com.abt.material.model.StockOrderRequestForm;
import com.abt.material.model.WarehouseRequestForm;
import com.abt.material.repository.*;
import com.abt.material.service.StockService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.abt.material.entity.StockOrder.STOCK_TYPE_IN;
import static com.abt.material.entity.StockOrder.STOCK_TYPE_OUT;

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

    public StockServiceImpl(StockOrderRepository stockOrderRepository, StockRepository stockRepository, WarehouseRepository warehouseRepository, MaterialDetailRepository materialDetailRepository, InventoryRepository inventoryRepository) {
        this.stockOrderRepository = stockOrderRepository;
        this.stockRepository = stockRepository;
        this.warehouseRepository = warehouseRepository;
        this.materialDetailRepository = materialDetailRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    @Override
    public void saveStockOrder(StockOrder stockOrder) {
        stockOrder = stockOrderRepository.save(stockOrder);
        List<Stock> list = new ArrayList<>();
        List<Inventory> inventories = new ArrayList<>();
        if (stockOrder.getStockList() != null) {
            for (Stock stock : stockOrder.getStockList()) {
                stock.setOrderId(stockOrder.getId());
                list.add(stock);

                //库存变化
                Inventory inv = inventoryRepository.findOneLatestInventory(stock.getMaterialId(), stockOrder.getWarehouseId())
                        .orElse(new Inventory(stock.getMaterialId(), stockOrder.getWarehouseId()));
                final Inventory newInv = changeInventoryQuantity(inv, stock, stockOrder.getStockType());
                inventories.add(newInv);
            }
        }
        list = stockRepository.saveAllAndFlush(list);
        stockOrder.setStockList(list);
        inventoryRepository.saveAllAndFlush(inventories);

    }

    private Inventory changeInventoryQuantity(Inventory inventory, Stock stock, int stockType) {
        Inventory newInventory = new Inventory();
        newInventory.setMaterialId(inventory.getMaterialId());
        newInventory.setWarehouseId(inventory.getWarehouseId());
        if (STOCK_TYPE_IN == stockType) {
            newInventory.setQuantity(inventory.getQuantity() + stock.getNum());
        } else if (STOCK_TYPE_OUT == stockType) {
            newInventory.setQuantity(inventory.getQuantity() - stock.getNum());
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
        final Page<Stock> page = stockRepository.findByQueryPageable(requestForm.getQuery(), requestForm.getStockType(), requestForm.getStartDate(), requestForm.getEndDate(), pageable);
        WithQueryUtil.build(page);
        return page;
    }


    @Override
    public void saveWarehouse(Warehouse warehouse) {
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
    public List<Inventory> latestInventories(InventoryRequestForm requestForm) {
        String wids = null, mids = null;
        if (requestForm.getWarehouseIds() != null) {
            wids = String.join(",", requestForm.getWarehouseIds());
        }
        if (requestForm.getMaterialTypeIds() != null) {
            mids = String.join(",", requestForm.getMaterialTypeIds());
        }
        return WithQueryUtil.build(inventoryRepository
                .findLatestInventory(mids, wids, Sort.by(Sort.Order.asc("warehouseId"), Sort.Order.asc("materialId"))));
    }


}
