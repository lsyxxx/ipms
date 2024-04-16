package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.PayVoucherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 款项支付单
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/wf/pay")
public class PayVoucherController {
    private final PayVoucherService payVoucherService;


    @PostMapping("/apply")
    public R<Object> apply(@Validated(ValidateGroup.Apply.class) @RequestBody PayVoucher payVoucher) {
        setSubmitUser(payVoucher);
        payVoucherService.apply(payVoucher);
        return R.success();
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody PayVoucher payVoucher) {
        setSubmitUser(payVoucher);
        payVoucherService.approve(payVoucher);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<PayVoucher>> todoList(@ModelAttribute PayVoucherRequestForm requestForm) {
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称 合同编号
        setUser(requestForm);
        final List<PayVoucher> todo = payVoucherService.findMyTodoByCriteria(requestForm);
        final int total = payVoucherService.countMyTodoByCriteria(requestForm);
        return R.success(todo, total);
    }

    @GetMapping("/done")
    public R<List<PayVoucher>> doneList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final List<PayVoucher> done = payVoucherService.findMyDoneByCriteriaPageable(requestForm);
        final int total = payVoucherService.countMyDoneByCriteria(requestForm);
        return R.success(done, total);
    }

    @GetMapping("/myapply")
    public R<List<PayVoucher>> myApplyList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final List<PayVoucher> myApplyList = payVoucherService.findMyApplyByCriteriaPageable(requestForm);
        final int total = payVoucherService.countMyApplyByCriteria(requestForm);
        return R.success(myApplyList, total);
    }

    @GetMapping("/all")
    public R<List<PayVoucher>> all(@ModelAttribute PayVoucherRequestForm requestForm) {
        final List<PayVoucher> all = payVoucherService.findAllByCriteriaPageable(requestForm);
        final int total = payVoucherService.countAllByCriteria(requestForm);
        return R.success(all, total);
    }

    @GetMapping("/load/{id}")
    public R<PayVoucher> load(@PathVariable String id) {
        final PayVoucher payVoucher = payVoucherService.loadEntityWithCurrentTask(id);
        return R.success(payVoucher);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        payVoucherService.delete(id);
        return R.success("删除成功");
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@Validated(ValidateGroup.Preview.class) @RequestBody PayVoucher payVoucher) {
        final List<UserTaskDTO> preview = payVoucherService.preview(payVoucher);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = payVoucherService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setSubmitUser(PayVoucher payVoucher) {
        UserView user = TokenUtil.getUserFromAuthToken();
        payVoucher.setSubmitUserid(user.getId());
        payVoucher.setSubmitUsername(user.getName());
    }

    public void setUser(PayVoucherRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }







}
