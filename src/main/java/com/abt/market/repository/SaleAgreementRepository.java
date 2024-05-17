package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SaleAgreementRepository extends JpaRepository<SaleAgreement, String>, JpaSpecificationExecutor<SaleAgreement> {
}