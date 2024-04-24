package com.abt.testing.service;

import com.abt.testing.entity.Agreement;
import com.abt.testing.model.AgreementRequestForm;
import com.abt.testing.repository.AgreementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgreementServiceImplTest {

    @Autowired
    private AgreementServiceImpl agreementService;

    @Test
    void findAgreementsPagedBy() {
        AgreementRequestForm form = new AgreementRequestForm();
        form.setPage(1);
        form.setLimit(2);
        form.setQuery("西北大学");
        final Page<Agreement> paged = agreementService.findAgreementsPagedBy(form);
        assertNotNull(paged);
        System.out.println("total: " + paged.getTotalElements());
        paged.getContent().forEach(i -> {
            System.out.println(i.toString());
        });
    }
}