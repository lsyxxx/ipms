package com.abt.sys.repository;

import com.abt.sys.model.entity.CustomerInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, String>, JpaSpecificationExecutor<CustomerInfo> {
    String isAtiveTrue = "1";
    List<CustomerInfo> findAllByIsAvtiveIs(String isAtive);
}