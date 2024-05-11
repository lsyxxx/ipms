package com.abt.sys.repository;

import com.abt.sys.model.entity.SupplyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplyInfoRepository extends JpaRepository<SupplyInfo, String> {


    @Query("SELECT DISTINCT s.supplierName FROM SupplyInfo s where s.isActive = '1'")
    List<String> findDistinctActiveSupplierNames();


}