package com.abt.market.service;

import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SaleAgreementService {
    Page<SaleAgreement> findPaged(SaleAgreementRequestForm requestForm);

    void delete(String id);

    List<String> findAllPartyA();

    List<SaleAgreement> findSaleAgreementCreatedByCurrentWeek();

    List<SaleAgreement> findSaleAgreementCreatedByCurrentMonth();

    List<SaleAgreement> findSaleAgreementCreatedByCurrentYear();

    long countAllSaleAgreement();

    void saveSaleAgreement(SaleAgreement saleAgreement);

    SaleAgreement LoadSaleAgreement(String id);
}
