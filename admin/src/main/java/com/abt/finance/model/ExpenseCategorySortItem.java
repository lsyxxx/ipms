package com.abt.finance.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 费用分类排序调整项
 */
@Getter
@Setter
public class ExpenseCategorySortItem {

    @NotBlank(message = "分类ID不能为空")
    private String id;

    @NotNull(message = "排序号不能为空")
    private Integer sortNo;
}
