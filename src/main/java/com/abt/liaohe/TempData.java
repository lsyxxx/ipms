package com.abt.liaohe;

import lombok.Data;

import java.time.LocalDate;

/**
 * 临时表数据
 */
@Data
public class TempData {

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
     * 检测项目名称,系统中
     */
    private String testName;

    /**
     * 检测项目code，系统中
     */
    private String testCode;

    /**
     * 检测项目结果，统一用string
     */
    private String testResult;

    /**
     * 检测日期
     */
    private LocalDate testDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 报告文件名
     */
    private String reportName;
}
