package com.abt.finance.service;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.model.CreditBookRequestForm;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CreditBookService {
    void saveCreditBook(CreditBook creditBook);

    Page<CreditBook> findBySpecification(CreditBookRequestForm form);

    /**
     * 资金流出详情
     */
    Optional<CreditBook> findById(String id);
}
