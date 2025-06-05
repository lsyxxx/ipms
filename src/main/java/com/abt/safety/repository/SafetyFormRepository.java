package com.abt.safety.repository;

import com.abt.safety.entity.SafetyForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SafetyFormRepository extends JpaRepository<SafetyForm, String>, JpaSpecificationExecutor<SafetyForm> {
    @Transactional
    @Modifying
    @Query("UPDATE SafetyForm SET isDeleted = TRUE WHERE id = :id")
    void logicDeleteById(String id);

    @Transactional
    @Modifying
    @Query("UPDATE SafetyForm SET enabled = :enabled WHERE id = :id")
    void updateEnabled(String id, boolean enabled);

    @Query("""
    select f from SafetyForm f left join fetch f.items where f.id = :id
""")
    Optional<SafetyForm> findWithItemsById(String id);

    //报错 使用了concat('%", query, '%')
//    @Query("""
//    select f from SafetyForm f
//    where f.isDeleted = false
//    and (:query is null or :query = '' or f.location like :query or f.name like :query or f.responsibleJno like :query or f.responsibleName like :query)
//""")
//    Page<SafetyForm> searchByQueryPaged(String query, Pageable pageable);


    @Query("""
            select (count(f) > 0) from SafetyForm f
            where f.isDeleted = false
            and f.location = :location
            and (:id = null or f.id <> :id)
            """)
    boolean checkLocationExists(String location, Long id);
}