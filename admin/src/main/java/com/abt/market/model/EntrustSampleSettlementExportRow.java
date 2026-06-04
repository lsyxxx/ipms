package com.abt.market.model;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntrustSampleSettlementExportRow {
    @ExcelProperty("项目编号")
    private String entrustId;

    @ExcelProperty("合同编号")
    private String contractNo;

    @ExcelProperty("甲方")
    private String clientName;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("样品编号")
    private String sampleRegistId;

    @ExcelProperty("检测项目编码")
    private String checkModeuleId;

    @ExcelProperty("检测项目名称")
    private String checkModeuleName;

    @ExcelProperty("是否结算")
    private String settledStatus;

    @ExcelProperty("结算单号")
    private String settlementIds;
}
