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
    public R<Object> apply(@Validated(ValidateGroup.Apply.class) @RequestBody InvoiceApply form) {
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
        return R.success(todo, todo.size());
    }

    @GetMapping("/done")
    public R<List<InvoiceApply>> doneList(InvoiceApplyRequestForm requestForm) {
        final List<InvoiceApply> done = invoiceApplyService.findMyDoneByCriteriaPageable(requestForm);
        return R.success(done, done.size());
    }

    @GetMapping("/myapply")
    public R<List<InvoiceApply>> myApplyList(InvoiceApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<InvoiceApply> myApplyList = invoiceApplyService.findMyApplyByCriteriaPageable(requestForm);
        return R.success(myApplyList, myApplyList.size());
    }

    @GetMapping("/all")
    public R<List<InvoiceApply>> all(InvoiceApplyRequestForm requestForm) {
        final List<InvoiceApply> all = invoiceApplyService.findAllByCriteriaPageable(requestForm);
        return R.success(all, all.size());
    }

    @GetMapping("/load/{id}")
    public R<InvoiceApply> load(@PathVariable String id) {
        final InvoiceApply InvoiceApply = invoiceApplyService.load(id);
        return R.success(InvoiceApply);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        invoiceApplyService.delete(id);
        return R.success("删除成功");
    }

    @GetMapping("/preview")
    public R<List<UserTaskDTO>> preview(@Validated(ValidateGroup.Preview.class) @RequestBody InvoiceApply InvoiceApply) {
        final List<UserTaskDTO> preview = invoiceApplyService.preview(InvoiceApply);
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
