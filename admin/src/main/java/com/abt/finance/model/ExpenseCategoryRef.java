package com.abt.finance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 业务单据上的费用分类快照（仅 code/name，不存主数据 id）
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryRef {

    @JsonProperty("expenseCategoryCode")
    @Column(name = "exp_cat_code", length = 64)
    private String code;

    @JsonProperty("expenseCategoryName")
    @Column(name = "exp_cat_name", length = 128)
    private String name;
}
