package com.abt.finance.service.impl;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.service.ICashCreditService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资金流出
 */
@Service
public class CashCreditServiceImpl {


    private final Map<String, ICashCreditService> creditServiceMap;

    public CashCreditServiceImpl(Map<String, ICashCreditService> creditServiceMap) {
        this.creditServiceMap = creditServiceMap;
    }


    public List<CreditBook> load() {
        List<CreditBook> books = new ArrayList<>();
        for (ICashCreditService cashCreditService : creditServiceMap.values()) {
            final List<CreditBook> creditBooks = cashCreditService.loadCreditBook();
            books.addAll(creditBooks);
        }
        return books;
    }

}
