package com.abt.finance.service;

import com.abt.finance.entity.Invoice;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发票
 */
public interface InvoiceService {

    List<Invoice> save(List<Invoice> invoices);

    List<Invoice> checkAndSave(List<Invoice> invoices);

    boolean hasError(Invoice invoice);

    Invoice checkOne(Invoice invoice);

    List<Invoice> check(List<Invoice> list);

    List<Invoice> findByRefCode(String refCode);

    void delete(String id);

    void deleteByRef(String refCode, String refName);

    void delete(List<String> list);
}
