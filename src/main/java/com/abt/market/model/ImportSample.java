package com.abt.market.model;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * excel导入样品
 */
@Getter
@Setter
public class ImportSample {

    @ExcelProperty("检测编号")
    private String sampleNo;

    @ExcelProperty("检测项目代码")
    private String checkModuleId;

    @ExcelProperty("检测项目名称")
    private String checkModuleName;

    @ExcelProperty("单价")
    private String price;
}
