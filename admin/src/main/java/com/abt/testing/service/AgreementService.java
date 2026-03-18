package com.abt.testing.service;

import com.abt.sys.model.entity.CustomerInfo;
import com.abt.testing.entity.Agreement;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.model.AgreementRequestForm;
import com.abt.testing.model.CustomerRequestForm;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AgreementService {
    /**
     * 保存预登记合同
     */
    void savePreAgreement(Agreement agreement);

    void deletePreAgreement(String entityId);

    Agreement load(String id);

    Page<Agreement> findAgreementsPagedBy(AgreementRequestForm requestForm);

    List<EnumLib> findAgreementEnumTypes();

}
