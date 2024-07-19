package com.abt.security;

import com.abt.common.util.TokenUtil;
import com.abt.sys.service.SysLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 访问记录
 */
@Order(1)
@Slf4j
@Component
public class SysLogFilter extends OncePerRequestFilter {

    private final SysLogService sysLogService;

    public SysLogFilter(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("SysLogFilter: URL: {}, IP: {} ", request.getRequestURI(), request.getRemoteAddr());
        try {
            sysLogService.saveUserRequestLog(request.getRemoteAddr(), request.getRequestURI(), TokenUtil.getUserFromAuthToken().getAccount());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
