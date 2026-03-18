package com.abt.salary;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器session验证
 * 我的工资
 */
@Slf4j
public class SalarySessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("========== SalarySessionInterceptor =================== ");
        final String requestURI = request.getRequestURI();
//        System.out.println("==== 拦截的url: " + requestURI);
//        final HttpSession session = request.getSession(false);


        return true; // 返回true表示放行请求
    }

}
