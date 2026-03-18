package com.abt.sys.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义权限提示
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String message = determineMessage(request);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }

    private String determineMessage(HttpServletRequest request) {
        // 根据业务场景（如 URL）生成自定义提示消息
        if (request.getRequestURI().startsWith("/safety/record/dispatch")) {
            return "您无权进行安全检查调度操作！若需要请联系管理员。";
        } else {
            return "访问被拒绝：您没有足够的权限。";
        }
    }


}
