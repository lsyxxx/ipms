package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleAgreementRepository extends JpaRepository<SaleAgreement, String>, JpaSpecificationExecutor<SaleAgreement> {

    List<SaleAgreement> findByCreateDateBetweenOrderByCreateDateDesc(LocalDateTime startDate, LocalDateTime endDate);
}