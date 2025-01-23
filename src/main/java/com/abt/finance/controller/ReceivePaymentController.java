package com.abt.finance.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.entity.ReceivePaymentConfig;
import com.abt.finance.model.ReceivePaymentRequestForm;
import com.abt.finance.service.ReceivePaymentService;
import com.abt.wf.model.InvoiceApplyStat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
     * @param form 登记表单
     */
    @PostMapping("/save")
    public R<Object> registerPayment(@Validated({ValidateGroup.Save.class}) @RequestBody ReceivePayment form) {
        receivePaymentService.registerPayment(form);
        return R.success("回款登记成功");
    }

    /**
     * 读取一条回款记录
     * @param id id
     */
    @GetMapping("/load")
    public R<ReceivePayment> loadReceivePayment(String id) {
        final ReceivePayment receivePayment = receivePaymentService.loadWithAll(id);
        return R.success(receivePayment);
    }

    /**
     * 查询回款登记列表
     */
    @GetMapping("/page")
    public R<List<ReceivePayment>> findReceivePaymentList(ReceivePaymentRequestForm form) {
        final List<ReceivePayment> list = receivePaymentService.findByQuery(form);
        return R.success(list, list.size());
    }

    @PostMapping("/cfg/save")
    public R<Object> saveReceivePaymentConfig(@RequestBody List<ReceivePaymentConfig> configs) {
        receivePaymentService.saveConfig(configs);
        return R.success("保存配置成功!");
    }

    @GetMapping("/cfg/notify")
    public R<List<User>> findDefaultNotifyUsers() {
        final List<User> users = receivePaymentService.findDefaultNotifyUsers();
        return R.success(users);
    }


    @GetMapping("/find/notify")
    public R<List<ReceivePayment>> findByNotifyUsers(ReceivePaymentRequestForm requestForm) {
        requestForm.setNotifyUserid(TokenUtil.getUseridFromAuthToken());
        final Page<ReceivePayment> page = receivePaymentService.findByNotifyUsers(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @PostMapping("/paying/stats")
    public R<List<InvoiceApplyStat>> payingStats(@RequestBody ReceivePaymentRequestForm requestForm) {
        final Page<InvoiceApplyStat> page = receivePaymentService.payingStats(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

}
