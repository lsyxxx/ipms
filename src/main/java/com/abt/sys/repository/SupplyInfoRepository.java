package com.abt.sys.repository;

import com.abt.sys.model.entity.SupplyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyInfoRepository extends JpaRepository<SupplyInfo, String> {
    String isAtiveTrue = "1";
}