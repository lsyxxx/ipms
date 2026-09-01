package com.abt.finance.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.util.QueryUtil;
import com.abt.finance.entity.ExpenseCategory;
import com.abt.finance.model.ExpenseCategoryRequestForm;
import com.abt.finance.model.ExpenseCategorySortItem;
import com.abt.finance.repository.ExpenseCategoryRepository;
import com.abt.finance.service.ExpenseCategoryService;
import com.abt.sys.exception.BusinessException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 费用分类业务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Override
    public ExpenseCategory findById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new MissingRequiredParameterException("id");
        }
        return expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("费用分类不存在(id=" + id + ")"));
    }

    @Override
    public Page<ExpenseCategory> findPage(ExpenseCategoryRequestForm form) {
        PageRequest pageable = PageRequest.of(form.jpaPage(), form.forcePaged().getLimit(),
                Sort.by(Sort.Direction.ASC, "sortNo", "code"));
        return expenseCategoryRepository.findAll(buildSpecification(form), pageable);
    }

    @Override
    public List<ExpenseCategory> findList(ExpenseCategoryRequestForm form) {
        Sort sort = Sort.by(Sort.Direction.ASC, "sortNo", "code");
        return expenseCategoryRepository.findAll(buildSpecification(form), sort);
    }

    @Override
    @Transactional
    public ExpenseCategory save(ExpenseCategory expenseCategory) {
        validateBeforeSave(expenseCategory);
        if (StringUtils.isBlank(expenseCategory.getId())) {
            if (expenseCategory.getSortNo() <= 0) {
                expenseCategory.setSortNo(getNextSortNo());
            }
        } else {
            ExpenseCategory existing = findById(expenseCategory.getId());
            if (expenseCategory.getSortNo() <= 0) {
                expenseCategory.setSortNo(existing.getSortNo());
            }
        }
        return expenseCategoryRepository.save(expenseCategory);
    }

    @Override
    @Transactional
    public void updateEnabled(String id, boolean enabled) {
        if (StringUtils.isBlank(id)) {
            throw new MissingRequiredParameterException("id");
        }
        findById(id);
        expenseCategoryRepository.updateEnabled(id, enabled);
    }

    @Override
    @Transactional
    public void updateSortNo(List<ExpenseCategorySortItem> items) {
        if (CollectionUtils.isEmpty(items)) {
            throw new BusinessException("排序列表不能为空");
        }
        for (ExpenseCategorySortItem item : items) {
            if (StringUtils.isBlank(item.getId())) {
                throw new MissingRequiredParameterException("id");
            }
            if (item.getSortNo() == null || item.getSortNo() <= 0) {
                throw new BusinessException("排序号必须大于0");
            }
            ExpenseCategory category = findById(item.getId());
            category.setSortNo(item.getSortNo());
            expenseCategoryRepository.save(category);
        }
    }

    @Override
    public int getNextSortNo() {
        return expenseCategoryRepository.findMaxSortNo() + 1;
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (StringUtils.isBlank(id)) {
            throw new MissingRequiredParameterException("id");
        }
        findById(id);
        expenseCategoryRepository.deleteById(id);
        log.info("物理删除费用分类, id={}", id);
    }

    private static final int CODE_MAX_LENGTH = 64;
    private static final int NAME_MAX_LENGTH = 128;
    private static final int REMARK_MAX_LENGTH = 256;

    private void validateBeforeSave(ExpenseCategory expenseCategory) {
        if (StringUtils.isBlank(expenseCategory.getCode())) {
            throw new BusinessException("分类编号不能为空");
        }
        if (expenseCategory.getCode().length() > CODE_MAX_LENGTH) {
            throw new BusinessException("分类编号不能超过" + CODE_MAX_LENGTH + "个字符");
        }
        if (StringUtils.isBlank(expenseCategory.getName())) {
            throw new BusinessException("分类名称不能为空");
        }
        if (expenseCategory.getName().length() > NAME_MAX_LENGTH) {
            throw new BusinessException("分类名称不能超过" + NAME_MAX_LENGTH + "个字符");
        }
        if (StringUtils.isNotBlank(expenseCategory.getRemark())
                && expenseCategory.getRemark().length() > REMARK_MAX_LENGTH) {
            throw new BusinessException("备注不能超过" + REMARK_MAX_LENGTH + "个字符");
        }
        expenseCategoryRepository.findByCode(expenseCategory.getCode()).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), expenseCategory.getId())) {
                throw new BusinessException("分类编号已存在: " + expenseCategory.getCode());
            }
        });
        expenseCategoryRepository.findByName(expenseCategory.getName()).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), expenseCategory.getId())) {
                throw new BusinessException("分类名称已存在: " + expenseCategory.getName());
            }
        });
    }

    private Specification<ExpenseCategory> buildSpecification(ExpenseCategoryRequestForm form) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(form.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), QueryUtil.like(form.getCode())));
            }
            if (StringUtils.isNotBlank(form.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), QueryUtil.like(form.getName())));
            }
            if (StringUtils.isNotBlank(form.getQuery())) {
                String pattern = QueryUtil.like(form.getQuery());
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("code"), pattern),
                        criteriaBuilder.like(root.get("name"), pattern)
                ));
            }
            if (form.getEnabledFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), form.getEnabledFilter()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
