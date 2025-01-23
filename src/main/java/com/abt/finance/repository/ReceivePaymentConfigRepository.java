package com.abt.finance.repository;

import com.abt.finance.entity.ReceivePaymentConfig;
import jakarta.persistence.Transient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReceivePaymentConfigRepository extends JpaRepository<ReceivePaymentConfig, String> {
    List<ReceivePaymentConfig> findByType(String type);

    @Modifying
    @Transactional
    void deleteByType(String type);
}