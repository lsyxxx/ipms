package com.abt.finance.repository;

import com.abt.finance.entity.FixedAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FixedAssetRepository extends JpaRepository<FixedAsset, String> {

    FixedAsset findByCode(String code);

    FixedAsset findFirstByOrderByCreateDateDesc();


    @Query("select fa from FixedAsset fa " +
            "where 1=1 " +
            "and (:assetType is null or :assetType = '' or fa.assetType = :assetType) " +
            "and (:dept is null or :dept = '' or fa.usageDept like %:dept%) " +
            "and (:query is null or :query = '' " +
            "   or fa.name like %:query% " +
            ") " +
            "order by fa.id asc")
    Page<FixedAsset> findByQuery(String query, String assetType, String dept, Pageable pageable);
}