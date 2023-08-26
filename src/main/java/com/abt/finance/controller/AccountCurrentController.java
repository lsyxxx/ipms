package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.finance.model.ReceivableDto;
import com.abt.finance.service.AccountCurrentService;
import com.abt.http.dto.WebApiToken;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 往来账 （收付款）
 */
@RestController
@Slf4j
@RequestMapping("/fi/cur")
@Tag(name = "AccountCurrentController", description = "往来账（收付款）")
public class AccountCurrentController {
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();
    private final UserService<UserView, WebApiToken> userService;

    private final AccountCurrentService accountCurrentService;

    public AccountCurrentController(UserService<UserView, WebApiToken> userService, AccountCurrentService accountCurrentService) {
        this.userService = userService;
        this.accountCurrentService = accountCurrentService;
    }

    @Operation(summary = "查看收款列表")
    @Parameter(name = "recType", description = "收款类型")
    @GetMapping("/rec")
    public R<String> receivable (@NotNull @RequestParam(required = false) String recType) {
        log.info("查询收款单列表 -- 收款类型:{}", recType);
        //User
        UserView user = TokenUtil.getUserFromAuthToken();
        //TODO: 处理业务，获取收款列表
        List<ReceivableDto> list = accountCurrentService.getReceivables(recType);
//        return R.success(list);
        return R.success("Get Account Receivables success! - receivableType=" + recType);
    }
}
