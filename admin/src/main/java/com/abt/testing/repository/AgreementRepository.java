package com.abt.testing.repository;

import com.abt.testing.entity.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Collection;
import java.util.List;

public interface AgreementRepository extends JpaRepository<Agreement, String>, JpaSpecificationExecutor<Agreement> {
    boolean existsByAgreementCode(String agreementCode);
    Agreement findByAgreementCode(String agreementCode);

    List<Agreement> findByAgreementCodeIn(Collection<String> agreementCodes);

}
