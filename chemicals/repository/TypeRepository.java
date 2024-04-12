package com.abt.chemicals.repository;

import com.abt.chemicals.entity.ChemicalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TypeRepository extends JpaRepository<ChemicalType, String> {

    List<ChemicalType> findByLevelAndNameContainingOrderBySortAsc(@Param("level_") int level, @Param("name_") String name);
    List<ChemicalType> findByLevel(@Param("level_") int level);

    List<ChemicalType> findByLevelAndEnableOrderBySortAsc(@Param("level_") int level, @Param("enable") boolean enable);

    @Modifying
    @Transactional
    long deleteByParentId(@Param("parent_id") String parentId);

}
