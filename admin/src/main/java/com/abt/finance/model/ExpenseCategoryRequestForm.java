package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 * 费用分类查询参数
 */
@Getter
@Setter
public class ExpenseCategoryRequestForm extends RequestForm {

    /**
     * 分类编号（模糊查询）
     */
    private String code;

    /**
     * 是否启用筛选；不传则查询全部（含启用与禁用）
     */
    private Boolean enabledFilter;
}
