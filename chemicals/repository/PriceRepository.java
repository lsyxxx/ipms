package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, String> {

    long deleteByCompanyId(@Param("companyId") String companyId);
    List<Price> findByChemicalIdOrderByCompanyIdAsc(String chemicalId);
}
