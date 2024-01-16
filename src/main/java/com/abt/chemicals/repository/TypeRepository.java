package com.abt.chemicals.repository;

import com.abt.chemicals.entity.ChemicalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TypeRepository extends JpaRepository<ChemicalType, String> {

    List<ChemicalType> findByLevelAndNameLikeOrderBySortAsc(@Param("level") int level, @Param("word") String word);
}
