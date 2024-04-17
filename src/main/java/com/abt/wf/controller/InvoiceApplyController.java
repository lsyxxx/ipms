package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.InvoiceApplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 开票申请
 */
@RestController
@Slf4j
@RequestMapping("/wf/inv")
@AllArgsConstructor
public class InvoiceApplyController {

    private final InvoiceApplyService invoiceApplyService;;

    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody InvoiceApply form) {
        invoiceApplyService.apply(form);
        return R.success();
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody InvoiceApply form) {
        invoiceApplyService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<InvoiceApply>> todoList(@ModelAttribute InvoiceApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称，合同编号, 客户id， 客户name, 项目名称，申请部门id, 申请部门name
        final List<InvoiceApply> todo = invoiceApplyService.findMyTodoByCriteria(requestForm);
        final int total = invoiceApplyService.countMyTodoByCriteria(requestForm);
        return R.success(todo, todo.size());
    }

    @GetMapping("/done")
    public R<List<InvoiceApply>> doneList(InvoiceApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<InvoiceApply> done = invoiceApplyService.findMyDoneByCriteriaPageable(requestForm);
        final int total = invoiceApplyService.countMyDoneByCriteria(requestForm);
        return R.success(done, total);
    }

    @GetMapping("/myapply")
    public R<List<InvoiceApply>> myApplyList(InvoiceApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<InvoiceApply> myApply = invoiceApplyService.findMyApplyByCriteriaPageable(requestForm);
        final int total = invoiceApplyService.countMyApplyByCriteria(requestForm);
        return R.success(myApply, total);
    }

    @GetMapping("/all")
    public R<List<InvoiceApply>> all(@ModelAttribute InvoiceApplyRequestForm requestForm) {
        final List<InvoiceApply> all = invoiceApplyService.findAllByCriteriaPageable(requestForm);
        final int total = invoiceApplyService.countAllByCriteria(requestForm);
        return R.success(all, total);
    }

    @GetMapping("/load/{id}")
    public R<InvoiceApply> load(@PathVariable String id) {
        InvoiceApply invoiceApply = invoiceApplyService.getEntityWithCurrentTask(id);
        return R.success(invoiceApply);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        invoiceApplyService.delete(id);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody InvoiceApply form) {
        final List<UserTaskDTO> preview = invoiceApplyService.preview(form);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = invoiceApplyService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setTokenUser(InvoiceApplyRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

}
