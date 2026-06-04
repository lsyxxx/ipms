package com.abt.market.model;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntrustSettlementExportRow {
    @ExcelProperty("项目编号")
    private String entrustId;

    @ExcelProperty("合同编号")
    private String contractNo;

    @ExcelProperty("甲方")
    private String clientName;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("结算情况")
    private String settlementStatus;

    @ExcelProperty("结算单号")
    private String settlementIds;
}
