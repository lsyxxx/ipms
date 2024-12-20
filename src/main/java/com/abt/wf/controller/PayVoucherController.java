package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.PayVoucherService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 款项支付单
 */
@RestController
@Slf4j
@RequestMapping("/wf/pay")
public class PayVoucherController {
    private final PayVoucherService payVoucherService;
    @Value("${abt.pay.excel.template}")
    private String excelTemplate;

    public PayVoucherController(PayVoucherService payVoucherService) {
        this.payVoucherService = payVoucherService;
    }

    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        payVoucherService.revoke(id, user.getId(), user.getName());
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<PayVoucher> copyEntity(String id) {
        final PayVoucher copyEntity = payVoucherService.getCopyEntity(id);
        return R.success(copyEntity);
    }

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
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<PayVoucher>> doneList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<PayVoucher>> myApplyList(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<PayVoucher>> all(@ModelAttribute PayVoucherRequestForm requestForm) {
        setUser(requestForm);
        final Page<PayVoucher> page = payVoucherService.findAllByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<PayVoucher> load(@PathVariable String id) {
        final PayVoucher payVoucher = payVoucherService.loadEntityWithCurrentTask(id);
        return R.success(payVoucher);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, @RequestParam(required = false) String reason) {
        payVoucherService.delete(id, reason);
        return R.success("删除成功");
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@Validated(ValidateGroup.Preview.class) @RequestBody PayVoucher payVoucher) {
        setSubmitUser(payVoucher);
        final List<UserTaskDTO> preview = payVoucherService.preview(payVoucher);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = payVoucherService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    @GetMapping("/export")
    public void export(PayVoucherRequestForm requestForm, HttpServletResponse response) throws IOException {
        try {
            requestForm.setUserid(TokenUtil.getUseridFromAuthToken());
            payVoucherService.export(requestForm, response, excelTemplate,"pay_export.xlsx", PayVoucher.class);
        } catch (IOException e) {
            final R<Object> fail = R.fail("导出失败!");
            response.getWriter().println(JsonUtil.toJson(fail));
        }
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
