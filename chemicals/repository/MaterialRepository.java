package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, String> {

    long deleteByChemicalId(@Param("chemicalId") String chemicalId);

    List<Material> findByChemicalIdAndTypeOrderByNameAsc(@Param("chemicalId") String chemicalId, @Param("type") String type);

    List<Material> findByChemicalId(@Param("chemicalId") String chemicalId);

}
