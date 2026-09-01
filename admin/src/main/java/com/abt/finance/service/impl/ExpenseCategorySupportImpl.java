package com.abt.finance.service.impl;

import com.abt.finance.entity.ExpenseCategory;
import com.abt.finance.model.ExpenseCategoryRef;
import com.abt.finance.model.WithExpenseCategory;
import com.abt.finance.repository.ExpenseCategoryRepository;
import com.abt.finance.service.ExpenseCategorySupport;
import com.abt.sys.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 费用分类快照填充与校验
 */
@Service
@RequiredArgsConstructor
public class ExpenseCategorySupportImpl implements ExpenseCategorySupport {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Override
    public void fillAndValidate(WithExpenseCategory target, String categoryId) {
        if (StringUtils.isBlank(categoryId)) {
            return;
        }
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("费用分类不存在(id=" + categoryId + ")"));
        if (!category.isEnabled()) {
            throw new BusinessException("费用分类已禁用: " + category.getName());
        }
        ExpenseCategoryRef ref = target.ensureRef();
        ref.setCode(category.getCode());
        ref.setName(category.getName());
    }
}
