package com.abt.finance.service;

import com.abt.finance.model.WithExpenseCategory;

/**
 * 费用分类快照填充与校验
 */
public interface ExpenseCategorySupport {

    /**
     * 根据主数据 id 填充 code/name 快照；id 为空时不修改已有快照
     *
     * @param target     业务实体
     * @param categoryId 费用分类主数据 id（Transient 入参）
     */
    void fillAndValidate(WithExpenseCategory target, String categoryId);
}
