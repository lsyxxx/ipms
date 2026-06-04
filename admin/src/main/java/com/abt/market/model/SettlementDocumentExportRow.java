package com.abt.market.model;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SettlementDocumentExportRow {
    @ExcelProperty("结算单号")
    private String settlementId;

    @ExcelProperty("结算单位")
    private String settlementUnit;

    @ExcelProperty("客户")
    private String clientName;

    @ExcelProperty("结算金额")
    private BigDecimal totalAmount;

    @ExcelProperty("创建人")
    private String createUsername;

    @ExcelProperty("创建日期")
    private LocalDateTime createDate;

    @ExcelProperty("附件名称")
    private String attachmentNames;

    @ExcelProperty("开票状态")
    private String invoiceStatus;
}
