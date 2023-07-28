package com.ipms.security;

import com.ipms.common.model.R;
import com.ipms.sys.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


/**
 * 登录认证失败后处理方法
 * 返回失败json信息
 */
@Slf4j
@Configuration
public class UserLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("UserLoginFailureHandler.onAuthenticationFailure() - {}", exception.getMessage());
        ResponseUtil.returnJson(response, R.fail(exception.getMessage()).toJson());
    }
}
