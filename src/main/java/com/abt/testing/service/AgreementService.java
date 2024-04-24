package com.abt.testing.service;

import com.abt.testing.entity.Agreement;
import com.abt.testing.model.AgreementRequestForm;
import org.springframework.data.domain.Page;

public interface AgreementService {
    /**
     * 保存预登记合同
     */
    void savePreAgreement(Agreement agreement);

    void deletePreAgreement(String entityId);

    Agreement load(String id);

    Page<Agreement> findAgreementsPagedBy(AgreementRequestForm requestForm);
}
