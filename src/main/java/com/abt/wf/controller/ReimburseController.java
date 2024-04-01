package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.RequestForm;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.BookKeepingService;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.service.impl.ReimburseServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.common.model.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/wf/rbs")
@Tag(name = "ReimburseController", description = "报销")
public class ReimburseController {

    private final ReimburseService reimburseService;

    public ReimburseController(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    /**
     * 获取业务流程数据
     * @param entityId 业务实体id
     */
    @GetMapping("/load/{entityId}")
    public R<ReimburseForm> load(@PathVariable String entityId) {
        final ReimburseForm rbs = reimburseService.loadReimburseForm(entityId);
        return R.success(rbs);
    }

    @PostMapping("/apply")
    public R<Object> apply(@Validated @RequestBody ReimburseForm form) {
        getUserFromToken(form);
        setServiceName(form);
        form.setRbsDate(LocalDate.now());
        reimburseService.apply(form);
        return R.success("流程申请成功");
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody ReimburseForm form) {
        getUserFromToken(form);
        setServiceName(form);
        reimburseService.approve(form);
        return R.success("审批成功");
    }

    /**
     * 撤销流程
     */
    @GetMapping("/revoke/{entityId}")
    public R<Object> revoke(@PathVariable String entityId) {
        reimburseService.revoke(entityId);
        return R.success("撤销成功");
    }

    @GetMapping("/delete/{entityId}")
    public R<Object> delete(@PathVariable String entityId) {
        reimburseService.delete(entityId);
        return R.success("删除成功");
    }


    /**
     * 流程记录
     */
    @GetMapping("/record/{entityId}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String entityId) {
        final List<FlowOperationLog> processRecord = reimburseService.processRecord(entityId);
        return R.success(processRecord, processRecord.size());
    }

    /**
     * 所有报销记录
     */
    @GetMapping("/all")
    public R<List<ReimburseForm>> allList(@ModelAttribute ReimburseRequestForm queryForm) {
        //query: 分页, 审批编号, 审批结果，审批状态，创建人，创建时间
        final List<ReimburseForm> all = reimburseService.findAllByCriteria(queryForm);
        return R.success(all, all.size());
    }

    /**
     * 我已处理的
     */
    @GetMapping("/done")
    public R<List<ReimburseForm>> myDone(@ModelAttribute ReimburseRequestForm queryForm) {
        // criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
        getUserFromToken(queryForm);
        final List<ReimburseForm> done = reimburseService.findMyDoneByCriteria(queryForm);
        return R.success(done, done.size());
    }

    /**
     * 待我处理
     */
    @GetMapping("/todo")
    public R<List<ReimburseForm>> todoList(@ModelAttribute ReimburseRequestForm queryForm) {
        getUserFromToken(queryForm);
        // criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
        final List<ReimburseForm> todo = reimburseService.findMyTodoByCriteria(queryForm);
        return R.success(todo, todo.size());
    }

    /**
     * 我申请的
     */
    @GetMapping("/myapply")
    public R<List<ReimburseForm>> myApply(@ModelAttribute ReimburseRequestForm queryForm) {
        getUserFromToken(queryForm);
//        setUser(queryForm);
        final List<ReimburseForm> myApply = reimburseService.findMyApplyByCriteria(queryForm);
        return R.success(myApply, myApply.size());
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@Validated(ValidateGroup.Preview.class) @RequestBody ReimburseForm form) {
        getUserFromToken(form);
        final List<UserTaskDTO> preview = reimburseService.preview(form);
        return R.success(preview, preview.size());
    }

    /**
     * 当前登录用户是否拥有记账权限
     */
    @GetMapping("/checkbk")
    public R<Boolean> isReimburseBookKeepingUser() {
        final boolean access = reimburseService.bookKeepAccess(TokenUtil.getUserFromAuthToken().getId());
        return R.success(access);
    }


    public void getUserFromToken(ReimburseForm form) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(userView.getId());
        form.setSubmitUsername(userView.getUsername());
    }

    public void getUserFromToken(ReimburseRequestForm form) {
        UserView userView = TokenUtil.getUserFromAuthToken();
        form.setUserid(userView.getId());
        form.setUsername(userView.getName());
    }

    public void setUser(ReimburseRequestForm form) {
        form.setUserid(form.getQueryUserid());
        form.setUsername(form.getQueryUsername());
    }

    public void setServiceName(ReimburseForm form) {
        form.setServiceName(ReimburseServiceImpl.SERVICE_NAME);
    }
}
