package com.ipms.security;

import com.ipms.common.model.R;
import com.ipms.sys.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 登录成功后处理
 */
@Slf4j
@Configuration
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login success - Authentication = {}", authentication.toString());
        //TODO 成功处理: token


        //成功返回json
        ResponseUtil.returnJson(response, R.success("Login success").toJson());

    }
}
