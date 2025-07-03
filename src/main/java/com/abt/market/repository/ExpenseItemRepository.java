package com.abt.market.repository;

import com.abt.market.entity.ExpenseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, String> {
    List<ExpenseItem> findByMidOrderBySortNo(String mid);

    void deleteByMid(String mid);
}