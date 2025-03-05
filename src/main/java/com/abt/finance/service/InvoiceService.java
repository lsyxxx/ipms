package com.abt.finance.service;

import com.abt.finance.entity.Invoice;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发票
 */
public interface InvoiceService {

    List<Invoice> save(List<Invoice> invoices);

    Invoice checkOne(Invoice invoice);

    List<Invoice> check(List<Invoice> list);
}
