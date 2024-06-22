package com.abt.salary.model;

import lombok.Data;

import java.util.List;

/**
 * 工资条详情
 */
@Data
public class SalaryDetail {

    /**
     * 标题
     */
    private String label = "";

    /**
     * 对应值
     */
    private String value = "";

    /**
     * 子节点
     */
    private List<SalaryDetail> children;

}
