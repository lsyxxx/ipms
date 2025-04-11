package com.abt.material.model;

import com.abt.material.entity.Stock;
import com.abt.wf.model.PurchaseSummaryAmount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 库存汇总表
 */
@Getter
@Setter
public class StockSummaryTable {

    /**
     * 采购汇总金额
     */
    private List<PurchaseSummaryAmount> purchaseSummaryAmountList;

    /**
     * 西安库房出入明细
     */
    private List<Stock> xianStockDetails;

    /**
     * 延安库房出入明细
     */
    private List<Stock> yananStockDetails;

    /**
     * 成都出入明细
     */
    private List<Stock> chengduStockDetails;

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
