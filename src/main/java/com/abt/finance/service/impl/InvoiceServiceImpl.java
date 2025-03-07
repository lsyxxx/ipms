package com.abt.finance.service.impl;

import com.abt.finance.entity.Invoice;
import com.abt.finance.repository.InvoiceRepository;
import com.abt.finance.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 发票
 */
@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<Invoice> save(List<Invoice> invoices) {
         return invoiceRepository.saveAllAndFlush(invoices);
    }

    @Override
    public List<Invoice> checkAndSave(List<Invoice> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return new ArrayList<>();
        }
        List<Invoice> errList = new ArrayList<>();
        List<Invoice> saved = new ArrayList<>();
        invoices.forEach(invoice -> {
            doCheck(invoice);
            if (hasError(invoice)) {
                errList.add(invoice);
            } else {
                saved.add(invoice);
            }
        });
        if (errList.isEmpty()) {
            invoiceRepository.saveAllAndFlush(saved);
        }
        return errList;
    }
    @Override
    public boolean hasError(Invoice invoice) {
        return StringUtils.isNotBlank(invoice.getError());
    }

    /**
     * 保存
     * @param invoice 发票
     */
    private Invoice save(Invoice invoice) {

        try {
            final long count = invoiceRepository.countById(invoice.getId());
            if (count > 0) {
                invoice.setError("发票号码(" + invoice.getId() + ")已存在！");
                return invoice;
            }
            Invoice save = invoiceRepository.save(invoice);
            clearError(invoice);
            return save;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            invoice.setError(e.getMessage());
            return invoice;
        }
    }


    private void prepareCheck(Invoice invoice) {
        clearError(invoice);
    }

    private void doCheck(Invoice invoice) {
        prepareCheck(invoice);
        final Optional<Invoice> byId = invoiceRepository.findById(invoice.getId());
        if (byId.isPresent()) {
            String error = "发票号码(" + invoice.getId() + ")已存在!";
            Invoice i = byId.get();
            invoice.setRefName(i.getRefName());
            invoice.setRefCode(i.getRefCode());
            invoice.setCreateUsername(i.getCreateUsername());
            invoice.setCreateDate(i.getCreateDate());
            if (StringUtils.isNotBlank(i.getRefName())) {
                error = error + "关联单据类型:" + i.getRefName() + ";";
            }
            if (StringUtils.isNotBlank(i.getRefCode())) {
                error = error + "单据编号:" + i.getRefCode() + ";";
            }
            invoice.setError(error);
        }
//        else {
//            invoice = invoiceRepository.save(invoice);
//        }
    }

    @Transactional
    @Override
    public Invoice checkOne(Invoice invoice) {
        prepareCheck(invoice);
        if (invoice.getId() != null) {
            doCheck(invoice);
        } else {
            invoice.setError("发票号码为空");
        }
        return invoice;
    }

    @Transactional
    @Override
    public List<Invoice> check(List<Invoice> list) {
        if (list != null && !list.isEmpty()) {
            list.forEach(this::doCheck);
        }
        return list;
    }
    private Invoice findCachedElementById(List<Invoice> list, String id) {
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return null;
        }
        for (Invoice invoice : list) {
            if (Objects.equals(id, invoice.getId())) {
                return invoice;
            }
        }
        return null;
    }

    private void clearError(Invoice invoice) {
        if (invoice == null) {
            return;
        }
        invoice.setError(null);
    }

    @Override
    public List<Invoice> findByRefCode(String refCode) {
        return invoiceRepository.findByRefCode(refCode);
    }

    @Override
    public void delete(String id) {
        invoiceRepository.deleteById(id);
    }

    @Override
    public void deleteByRef(String refCode, String refName) {
        invoiceRepository.deleteByRefCodeAndRefName(refCode, refName);
    }

    @Override
    public void delete(List<String> list) {
        invoiceRepository.deleteAllById(list);
    }
}
