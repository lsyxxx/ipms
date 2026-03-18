package com.abt.wxapp.login.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.login.service.LoginService;
import com.abt.wxapp.security.JwtUtil;
import com.abt.wxapp.security.WxUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class LoginController {

    private final LoginService loginService;

    /**
     * TODO: 微信账号登录/创建用户
     *
     * 小程序 wx.login() 官方api → code：
     *         ↓
     * POST /login/wx?code=xxx  注册/登录
     *         ↓
     * WxApiService.getOpenid(code)     → 调微信 API 获取 openid
     *         ↓
     * LoginService.findOrCreateUser()  → 查/建用户（todo: 接数据库）
     *         ↓
     * JwtUtil.generateToken()          → 生成 30 天 JWT
     *         ↓
     * 返回 Token 给小程序，本地存储。后续通过jwt来验证用户
     *
     * @param code 小程序前端调用 wx.login() 获取的临时凭证
     * @return JWT Token，客户端保存后在后续请求的 Authorization Header 中携带
     */
    @PostMapping("/wx")
    public R<String> wxLogin(String code) {
        loginService.wxLogin(code);

        //生成token
        JwtUtil  jwtUtil = new JwtUtil();
        String jwtToken = jwtUtil.generateToken(new WxUserDetails("", null, null));

        return R.success(jwtToken);
    }
}
