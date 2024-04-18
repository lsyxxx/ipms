package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Loan;
import com.abt.wf.model.LoanRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.LoanService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/wf/loan")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public R<Object> apply(@Validated(ValidateGroup.Apply.class) @RequestBody Loan loan) {
        loanService.apply(loan);
        return R.success("申请成功");
    }

    @GetMapping("/approve")
    public R<Object> approve(@RequestBody Loan loan) {
        loanService.approve(loan);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<Loan>> todoList(@ModelAttribute LoanRequestForm requestForm) {
        setTokenUser(requestForm);
        //criteria 申请人 申请日期（起止日期） 流程状态 审批编号 支付方式,项目，借款部门
        final List<Loan> todo = loanService.findMyTodoByCriteria(requestForm);
        final int total = loanService.countMyTodoByCriteria(requestForm);
        return R.success(todo, total);
    }

    @GetMapping("/done")
    public R<List<Loan>> doneList(LoanRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<Loan> done = loanService.findMyDoneByCriteriaPageable(requestForm);
        final int total = loanService.countMyDoneByCriteria(requestForm);
        return R.success(done, total);
    }

    @GetMapping("/myapply")
    public R<List<Loan>> myApplyList(LoanRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<Loan> myApplyList = loanService.findMyApplyByCriteriaPageable(requestForm);
        final int total = loanService.countMyApplyByCriteria(requestForm);
        return R.success(myApplyList, total);
    }

    @GetMapping("/all")
    public R<List<Loan>> all(LoanRequestForm requestForm) {
        final List<Loan> all = loanService.findAllByCriteriaPageable(requestForm);
        final int total = loanService.countAllByCriteria(requestForm);
        return R.success(all, total);
    }

    @GetMapping("/load/{id}")
    public R<Loan> load(@PathVariable String id) {
        final Loan Loan = loanService.loadWithActiveTask(id);
        return R.success(Loan);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        loanService.delete(id);
        return R.success("删除成功");
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@Validated(ValidateGroup.Preview.class) @RequestBody Loan Loan) {
        final List<UserTaskDTO> preview = loanService.preview(Loan);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = loanService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setTokenUser(LoanRequestForm requestForm) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        requestForm.setUserid(user.getId());
        requestForm.setUsername(user.getUsername());
    }
}
