package com.abt.finance.service;

import com.abt.finance.entity.ExpenseCategory;
import com.abt.finance.model.ExpenseCategoryRequestForm;
import com.abt.finance.model.ExpenseCategorySortItem;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 费用分类业务接口
 */
public interface ExpenseCategoryService {

    ExpenseCategory findById(String id);

    Page<ExpenseCategory> findPage(ExpenseCategoryRequestForm form);

    List<ExpenseCategory> findList(ExpenseCategoryRequestForm form);

    ExpenseCategory save(ExpenseCategory expenseCategory);

    void updateEnabled(String id, boolean enabled);

    void updateSortNo(List<ExpenseCategorySortItem> items);

    int getNextSortNo();

    void delete(String id);
}
