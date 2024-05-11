package com.abt.finance.repository;

import com.abt.finance.entity.AccountItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountItemRepository extends JpaRepository<AccountItem, String> {

    List<AccountItem> findAllByIsActiveOrderByClassCodeAsc(String isActive);
}