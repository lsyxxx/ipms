package com.abt.wf.model;

import com.abt.finance.entity.Invoice;

import java.util.List;

public interface WithInvoice {
    void setInvoiceList(List<Invoice> invoiceList);
    List<Invoice> getInvoiceList();

}
