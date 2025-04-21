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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        List<Stock> list = new ArrayList<>();
        List<Inventory> inventories = new ArrayList<>();
        if (stockOrder.getStockList() != null) {
            for (Stock stock : stockOrder.getStockList()) {
                stock.setOrderId(stockOrder.getId());
                list.add(stock);
                //库存变化
                Inventory inv = new Inventory();
                inv.setWarehouseId(stockOrder.getWarehouseId());
                inv.setMaterialId(stock.getMaterialId());
                if (STOCK_TYPE_CHECK != stockOrder.getStockType()) {
                    inv = inventoryRepository.findOneLatestInventory(stock.getMaterialId(), stockOrder.getWarehouseId())
                            .orElse(new Inventory(stock.getMaterialId(), stockOrder.getWarehouseId()));
                }
                Inventory newInv = changeInventoryQuantity(inv, stock, stockOrder.getStockType());
                newInv.setOrderId(stockOrder.getId());
                inventories.add(newInv);
            }
        }
        list = stockRepository.saveAllAndFlush(list);
        stockOrder.setStockList(list);
        inventoryRepository.saveAllAndFlush(inventories);
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
        } else if(STOCK_TYPE_CHECK == stockType) {
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
    public List<MaterialDetailDTO>  findAllMaterialInventories(InventoryRequestForm requestForm) {
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
        return WithQueryUtil.build(inventoryAlertRepository.findAllBy( requestForm.getWarehouseIds(), requestForm.getName()));
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
        //剩余库存(endDate前最近日期的库存，不包含endDate)
        final ArrayList<Stock> distinctList = stockList.stream().collect(collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(obj -> obj.getMaterialId() + "#" + obj.getWarehouseId())
                )),
                ArrayList::new
        ));

        Map<String, Inventory> invMap = new HashMap<>();

        for (Stock stock : distinctList) {
            final Inventory inv = inventoryRepository.findNearestBefore(endDate, stock.getMaterialId(), stock.getWarehouseId());
            invMap.put(stock.getMaterialId() + "#" + stock.getWarehouseId(), inv);
        }
        for (Stock stock : stockList) {
            final Inventory inv = invMap.get(stock.getMaterialId() + "#" + stock.getWarehouseId());
            if (inv != null) {
                stock.setInventory(inv.getQuantity());
            }
        }

        Comparator<Stock> comparator = new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
                return o1.getMaterialId().compareTo(o2.getMaterialId());
            }
        };

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


}
