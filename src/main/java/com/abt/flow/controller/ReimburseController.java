package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.*;

/**
 * 报销流程
 */
@RestController
@Slf4j
@RequestMapping("/wf/rb")
@Tag(name = "ReimburseController", description = "报销流程")
public class ReimburseController {
    private final ReimburseService reimburseService;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public ReimburseController(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    @Operation(summary = "报销流程申请")
    @Parameter(name = "applyForm", description = "申请业务数据form")
    @PostMapping("/apply")
    public R apply(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();

        reimburseService.apply(user, applyForm);

        return R.success(messages.getMessage("common.apply.success"));
    }

    @Operation(summary = "部门审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/dc")
    public R departmentCheck(@RequestBody @NotNull ReimburseApplyForm applyForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.departmentAudit(user, applyForm);
        return R.success();
    }


    @Operation(summary = "技术负责人审批")
    @Parameter(name = "applyForm", description = "业务数据form")
    @PostMapping("/tc")
    public R techCheck(@RequestBody @NotNull ReimburseApplyForm applyForm) {



        return R.success();
    }




}
