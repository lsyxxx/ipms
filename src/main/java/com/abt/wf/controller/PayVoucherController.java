package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.model.PayVoucherRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 款项支付单
 */
@RestController
@Slf4j
@RequestMapping("/wf/pay")
public class PayVoucherController {

    @PostMapping("/apply")
    public R<Object> apply(@Validated(ValidateGroup.Apply.class) @RequestBody PayVoucher payVoucher) {

        return null;
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody PayVoucher payVoucher) {
        return null;
    }

    @GetMapping("/todo")
    public R<List<PayVoucher>> todoList(PayVoucherRequestForm requestForm) {
        return null;
    }

    @GetMapping("/done")
    public R<List<PayVoucher>> doneList(PayVoucherRequestForm requestForm) {
        return null;
    }

    @GetMapping("/myapply")
    public R<List<PayVoucher>> myApplyList(PayVoucherRequestForm requestForm) {
        return null;
    }

    @GetMapping("/all")
    public R<List<PayVoucher>> all(PayVoucherRequestForm requestForm) {
        return null;
    }

    @GetMapping("/load/{id}")
    public R<PayVoucher> load(@PathVariable String id) {
        return null;
    }

    @GetMapping("/del/{id}")
    public R<PayVoucher> delete(@PathVariable String id) {
        return null;
    }

    @GetMapping("/preview")
    public R<PayVoucher> preview(@Validated(ValidateGroup.Preview.class) @RequestBody PayVoucher payVoucher) {
        return null;
    }

    @GetMapping("/record/{id}")
    public R<PayVoucher> processRecord(@PathVariable String id) {
        return null;
    }







}
