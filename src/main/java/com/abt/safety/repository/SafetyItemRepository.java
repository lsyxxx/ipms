package com.abt.safety.repository;

import com.abt.safety.entity.SafetyItem;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SafetyItemRepository extends JpaRepository<SafetyItem, String>, JpaSpecificationExecutor<SafetyItem> {
    @Transactional
    @Modifying
    @Query("UPDATE SafetyItem SET isDeleted = TRUE WHERE id = :id")
    void logicDeleteById(String id);

    @Transactional
    @Modifying
    @Query("UPDATE SafetyItem SET enabled = :enabled WHERE id = :id")
    void updateEnabled(String id, boolean enabled);

    @Query("""
        SELECT s FROM SafetyItem s
        WHERE s.isDeleted = FALSE
        AND (:enabled is null or s.enabled = :enabled)
        AND (:query is null or :query = '' or s.name like %:query%)
        """)
    Page<SafetyItem> findByQueryPageable(Boolean enabled, String query, Pageable pageable);

    @Query("SELECT s FROM SafetyItem s WHERE s.name = :name AND s.enabled = TRUE and s.isDeleted = FALSE")
    Optional<SafetyItem> findEnabledByName(String name);

    long countByIsDeletedFalse();
}