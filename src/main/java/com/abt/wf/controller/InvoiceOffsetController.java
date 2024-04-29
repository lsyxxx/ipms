package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.InvoiceOffsetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发票冲账申请
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/invoffset")
public class InvoiceOffsetController {
    private final InvoiceOffsetService invoiceOffsetService;

    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody InvoiceOffset form) {
        invoiceOffsetService.apply(form);
        return R.success();
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody InvoiceOffset form) {
        invoiceOffsetService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<InvoiceOffset>> todoList(@ModelAttribute InvoiceOffsetRequestForm requestForm) {
        setTokenUser(requestForm);
        //criteria
        final List<InvoiceOffset> todo = invoiceOffsetService.findMyTodoByCriteria(requestForm);
        final int total = invoiceOffsetService.countMyTodoByCriteria(requestForm);
        return R.success(todo, todo.size());
    }

    @GetMapping("/done")
    public R<List<InvoiceOffset>> doneList(InvoiceOffsetRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<InvoiceOffset> done = invoiceOffsetService.findMyDoneByCriteriaPageable(requestForm);
        final int total = invoiceOffsetService.countMyDoneByCriteria(requestForm);
        return R.success(done, total);
    }

    @GetMapping("/myapply")
    public R<List<InvoiceOffset>> myApplyList(InvoiceOffsetRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<InvoiceOffset> myApply = invoiceOffsetService.findMyApplyByCriteriaPageable(requestForm);
        final int total = invoiceOffsetService.countMyApplyByCriteria(requestForm);
        return R.success(myApply, total);
    }

    @GetMapping("/all")
    public R<List<InvoiceOffset>> all(@ModelAttribute InvoiceOffsetRequestForm requestForm) {
        final List<InvoiceOffset> all = invoiceOffsetService.findAllByCriteriaPageable(requestForm);
        final int total = invoiceOffsetService.countAllByCriteria(requestForm);
        return R.success(all, total);
    }

    @GetMapping("/load/{id}")
    public R<InvoiceOffset> load(@PathVariable String id) {
        InvoiceOffset invoiceApply = invoiceOffsetService.getEntityWithCurrentTask(id);
        return R.success(invoiceApply);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        invoiceOffsetService.delete(id);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody InvoiceOffset form) {
        final List<UserTaskDTO> preview = invoiceOffsetService.preview(form);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = invoiceOffsetService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setTokenUser(InvoiceOffsetRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }
}
