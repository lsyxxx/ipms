package com.abt.wxapp.login.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.login.service.LoginService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 */
@RestController
@Slf4j
@RequestMapping("/login")
@RequiredArgsConstructor
@Validated
public class LoginController {

    private final LoginService loginService;

    /**
     * 小程序 wx.login 获取 code 后调用，返回 JWT（Authorization: Bearer …）
     */
    @PostMapping("/wx")
    public R<String> wxLogin(@RequestParam("code") @NotBlank(message = "微信登录code不能为空") String code) {
        return R.success(loginService.wxLogin(code));
    }
}
