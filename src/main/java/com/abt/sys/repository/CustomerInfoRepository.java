package com.abt.sys.repository;

import com.abt.sys.model.entity.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, String>, JpaSpecificationExecutor<CustomerInfo> {
}