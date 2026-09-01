package com.abt.finance.repository;

import com.abt.finance.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 费用分类数据访问
 */
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, String>, JpaSpecificationExecutor<ExpenseCategory> {

    Optional<ExpenseCategory> findByCode(String code);

    Optional<ExpenseCategory> findByName(String name);

    @Query("select coalesce(max(e.sortNo), 0) from ExpenseCategory e")
    int findMaxSortNo();

    @Modifying
    @Query("update ExpenseCategory e set e.enabled = :enabled where e.id = :id")
    void updateEnabled(String id, boolean enabled);
}
