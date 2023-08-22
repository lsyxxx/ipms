package com.ipms.finance.controller;

import com.ipms.common.model.R;
import com.ipms.sys.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * 往来账 （收付款）
 */
@RestController
@Slf4j
@RequestMapping("/fi/cur")
@Tag(name = "AccountCurrentController", description = "往来账（收付款）")
public class AccountCurrentController {
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @Operation(summary = "查看收款列表")
    @Parameter(name = "recType", description = "收款类型")
    @GetMapping("/rec")
    public R<String> receivable (@NotNull @RequestParam(required = false) String recType) {
        log.info("查询收款单列表 -- 收款类型:{}", recType);
        Assert.notNull(recType, this.messages.getMessage("AccountCurrentController.receivable"));
        //User
        Authentication token = SecurityContextHolder.getContext().getAuthentication();

        return R.success("Get Account Receivables success! - receivableType=" + recType);
    }
}
