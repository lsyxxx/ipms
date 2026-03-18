package com.abt.common.interceptor;

import com.abt.common.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 验证token
 */
@Component
public class TokenValidateInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String service = request.getParameter("service");
        String reqToken = request.getParameter("reqToken");
        TokenUtil.checkFormToken(request.getSession(), service, reqToken);
        return true;
    }
}
