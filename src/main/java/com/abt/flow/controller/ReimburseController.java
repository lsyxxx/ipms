package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.FlowRequestForm;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.service.FlowInfoService;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报销流程
 */
@RestController
@Slf4j
@RequestMapping("/wf/rb")
@Tag(name = "ReimburseController", description = "报销流程")
public class ReimburseController {
    private final ReimburseService reimburseService;
    private final FlowInfoService flowInfoService;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public ReimburseController(ReimburseService reimburseService, FlowInfoService flowInfoService) {
        this.reimburseService = reimburseService;
        this.flowInfoService = flowInfoService;
    }



    @Operation(summary = "报销流程申请")
    @Parameter(name = "applyForm", description = "申请业务数据form")
    @PostMapping("/apply")
    public R apply(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        //flowType: id/code/name
        //user
        //bizData: cost/rbsDate/reason/voucherNum/project
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.apply(user, applyForm);

        return R.success(messages.getMessage("common.apply.success"));
    }

    @Operation(summary = "部门审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/dpa")
    public R departmentAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.departmentAudit(user, applyForm);
        return R.success();
    }


    @Operation(summary = "技术负责人审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/tca")
    public R techAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.techLeadAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "财务主管审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/fma")
    public R financeManagerAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.financeManagerAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "税务会计审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/toa")
    public R taxOfficerAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.taxOfficerAudit(user, applyForm);

        return R.success();
    }

    @Operation(summary = "财务会计审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/ata")
    public R accountantAudit(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.accountantAudit(user, applyForm);

        return R.success();
    }


    @Operation(summary = "我的报销记录")
    @Parameter(name = "form", description = "查询条件表单(审批状态，结果)")
    @PostMapping("/myrbs")
    public R myReimburse(@RequestParam FlowRequestForm form) {

        UserView user = TokenUtil.getUserFromAuthToken();
        //TODO: 报销的type, 目前FlowScheme中的id，用code还是id?
        form.setType("7c2f7166-4696-4f4f-bf59-c4447179cbe3");

        List<FlowInfoVo> list = flowInfoService.getUserApplyFlows(form);

        return R.success(list, list.size());
    }






}
