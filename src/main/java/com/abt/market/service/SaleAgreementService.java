package com.abt.market.service;

import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import org.springframework.data.domain.Page;

public interface SaleAgreementService {
    Page<SaleAgreement> findPaged(SaleAgreementRequestForm requestForm);

    void delete(String id);
}
