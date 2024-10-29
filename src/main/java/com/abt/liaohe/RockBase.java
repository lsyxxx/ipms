package com.abt.liaohe;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 岩石分析基础
 */
@Getter
@Setter
@NoArgsConstructor
public class RockBase {

    /**
     * 检测编号
     * test id
     */
    private String tid;

    /**
     * 样品编号
     * sample id
     */
    private String sid;

    /**
     * 井号
     */
    private String wellNo;


    private String depth;

    /**
     * 顶界深度
     */
    private String mdTop;

    /**
     * 底界深度
     */
    private String mdBase;

    /**
     * 层位
     */
    @ExcelProperty(index = 5)
    private String layer;


    /**
     * 岩性
     */
    @ExcelProperty(index = 6)
    private String rockName;

}
