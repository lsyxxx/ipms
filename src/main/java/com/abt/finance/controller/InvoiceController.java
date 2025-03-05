package com.abt.finance.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.finance.entity.Invoice;
import com.abt.finance.service.InvoiceService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务发票相关
 */
@RestController
@Slf4j
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


    @PostMapping("/check/list")
    public R<List<Invoice>> invoiceCheck(@Validated({ValidateGroup.Apply.class}) @RequestBody List<Invoice> list, HttpSession session) {
        list = invoiceService.check(list);
        return R.success(list);
    }

    @PostMapping("/check/one")
    public R<Invoice> checkOne(@Validated({ValidateGroup.Apply.class}) @RequestBody Invoice invoice) {
        invoice = invoiceService.checkOne(invoice);
        return R.success(invoice);
    }

    @PostMapping("/save/list")
    public R<List<Invoice>  > save(@RequestBody List<Invoice> list) {
        final List<Invoice> error = invoiceService.save(list);
        if (error != null && !error.isEmpty()) {
            return R.success("保存成功!");
        }
        return R.fail(error, "保存失败!");
    }
}
