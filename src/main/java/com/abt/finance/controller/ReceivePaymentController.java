package com.abt.finance.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.model.ReceivePaymentRequestForm;
import com.abt.finance.service.ReceivePaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回款登记
 */
@RestController
@Slf4j
@RequestMapping("/fi/rp")
public class ReceivePaymentController {

    private final ReceivePaymentService receivePaymentService;

    public ReceivePaymentController(ReceivePaymentService receivePaymentService) {
        this.receivePaymentService = receivePaymentService;
    }


    /**
     * 登记
     *
     * @param form 登记表单
     */
    @PostMapping("/reg")
    public R<Object> registerPayment(@Validated({ValidateGroup.Save.class}) @RequestBody ReceivePayment form) {
        receivePaymentService.registerPayment(form);
        return R.success("回款登记成功");
    }

    /**
     * 读取一条回款记录
     * @param id id
     */
    @GetMapping("/load/recpay")
    public R<ReceivePayment> loadReceivePayment(String id) {
        final ReceivePayment receivePayment = receivePaymentService.loadRegisterRecord(id);
        return R.success(receivePayment);
    }

    /**
     * 查询回款登记列表
     */
    @GetMapping("/find/recpay/list")
    public R<List<ReceivePayment>> findReceivePaymentList(ReceivePaymentRequestForm form) {
        final List<ReceivePayment> list = receivePaymentService.findByQuery(form);
        return R.success(list, list.size());
    }


}
