package com.abt.finance.repository;

import com.abt.finance.entity.AccountItem;
import org.camunda.bpm.engine.impl.identity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountItemRepository extends JpaRepository<AccountItem, String> {
    List<AccountItem> findByEnabled(boolean enabled);
    List<AccountItem> findByLevel(int level);

    @Query("select ai from AccountItem ai " +
            "where 1=1 " +
            "and (:query is null or :query = '' " +
            "   or ai.code like %:query% " +
            "   or ai.name like %:query%) " +
            "and ai.enabled = :enabled " +
            "and (:level is null or ai.level = :level) ")
//            "ORDER BY CAST(ai.code AS int)")
    Page<AccountItem> findByQuery(String query, boolean enabled, Integer level, Pageable pageable);

    @Query("select ai from AccountItem ai " +
            "where 1=1 " +
            "and (:query is null or :query = '' " +
            "   or ai.code like %:query% " +
            "   or ai.name like %:query%) " +
            "and ai.enabled = :enabled " +
            "and (:level is null or ai.level = :level) ")
//            "ORDER BY CAST(ai.code AS int)")
    List<AccountItem> findByQuery(String query, boolean enabled,  Integer level);
}