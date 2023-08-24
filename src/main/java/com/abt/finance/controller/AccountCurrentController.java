package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.http.dto.WebApiToken;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    public AccountCurrentController(UserService<UserView, WebApiToken> userService) {
        this.userService = userService;
    }

    @Operation(summary = "查看收款列表")
    @Parameter(name = "recType", description = "收款类型")
    @GetMapping("/rec")
    public R<String> receivable (@NotNull @RequestParam(required = false) String recType) {
        log.info("查询收款单列表 -- 收款类型:{}", recType);
        //User
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        if (token.getCredentials() == null) {
            return R.invalidToken();
        }
        UserView user = (UserView) token.getPrincipal();
        log.info("从认证的token中获取user - {}", user.simpleInfo());

//        Optional<UserView> user = userService.userInfoBy(WebApiToken.of(token.getCredentials().toString()));
//        if (user.isPresent()) {
//            log.info(user.toString());
//        } else {
//            log.error("没有找到用户 by token: {}", token.getCredentials());
//            return R.fail("ex.token.user.notfound");
//        }

        return R.success("Get Account Receivables success! - receivableType=" + recType);
    }
}
