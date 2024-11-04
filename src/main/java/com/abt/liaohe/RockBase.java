package com.abt.liaohe;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 岩石分析基础
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class RockBase {

    /**
     * 检测编号
     * test id
     */
    @NotNull
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
    private String layer;


    /**
     * 岩性
     */
    private String rockName;



    /**
     * 检测日期
     */
    private String testDate;

    /**
     * 检测单位（我方）
     */
    private String company = "西安阿伯塔资环分析测试技术有限公司";

    private String remark;

    /**
     * 报告名称
     */
    private String reportName;

}
