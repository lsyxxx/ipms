package com.abt.wf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 外送汇总数据
 */
@Getter
@Setter
public class SbctSummaryData {
    private String entrustId;

    private String checkModuleId;

    private String checkModuleName;

    private Double price;

    private Double totalPrice;

    /**
     * 外送数量和
     */
    private Long applyNum;

    /**
     * 结算数量和
     */
    private int settledNum;

    private String error;

    private String remark;

    public SbctSummaryData() {
        super();
    }

    public SbctSummaryData(String entrustId, String checkModuleId, String checkModuleName, Long applyNum) {
        super();
        this.entrustId = entrustId;
        this.checkModuleId = checkModuleId;
        this.checkModuleName = checkModuleName;
        this.applyNum = applyNum;
    }
    public SbctSummaryData(String entrustId, String checkModuleId, String checkModuleName, Long applyNum, BigDecimal price,  BigDecimal totalPrice) {
        super();
        this.entrustId = entrustId;
        this.checkModuleId = checkModuleId;
        this.checkModuleName = checkModuleName;
        this.applyNum = applyNum;
        this.price = price.doubleValue();
        this.totalPrice = totalPrice.doubleValue();
    }
}
