package com.abt.finance.repository;

import com.abt.finance.entity.ReceivePaymentConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceivePaymentConfigRepository extends JpaRepository<ReceivePaymentConfig, String> {
    List<ReceivePaymentConfig> findByType(String type);
}