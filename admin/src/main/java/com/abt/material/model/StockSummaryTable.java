package com.abt.material.model;

import com.abt.material.entity.Stock;
import com.abt.wf.model.PurchaseSummaryAmount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 库存汇总表
 */
@Getter
@Setter
public class StockSummaryTable {

    /**
     * 礼品类出入库详情
     * key: 礼品类库房id
     * value: 库房出入明细
     */
    private Map<String, List<Stock>> giftStockMap;

    /**
     * 采购汇总金额
     */
    private List<PurchaseSummaryAmount> purchaseSummaryAmountList;

    /**
     * 入库汇总
     */
    private List<StockQuantitySummary> stockInSummary;
    /**
     * 出库汇总
     */
    private List<StockQuantitySummary> stockOutSummary;

    /**
     * 年度采购汇总
     */
    private List<PurchaseSummaryAmount> yearSummary;
}
