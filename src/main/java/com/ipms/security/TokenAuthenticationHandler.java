package com.ipms.security;

import com.ipms.sys.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *  token认证处理
 */
@Slf4j
@Component
public class TokenAuthenticationHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationFailureHandler,
        LogoutSuccessHandler, SessionInformationExpiredStrategy,
        AccessDeniedHandler, AuthenticationEntryPoint {

    /**
     * 用来解决匿名（无认证）用户访问 无权限资源时的异常
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param authException that caused the invocation
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("认证失败 -- {}", authException.getMessage());
        ResponseUtil.returnInvalidToken(response);
    }

    /**
     * Handles an access denied failure.
     * 没有授权
     * @param request that resulted in an <code>AccessDeniedException</code>
     * @param response so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access Denied! -- url: {}, message: {}", request.getRequestURI(), accessDeniedException.getMessage());
        ResponseUtil.returnInvalidToken(response);
    }


    /**
     * 认证失败处理
     * @param request the request during which the authentication attempt occurred.
     * @param response the response.
     * @param exception the exception which was thrown to reject the authentication
     * request.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.warn("认证失败! -- url: {}, message: {}", request.getRequestURI(), exception.getMessage());
        ResponseUtil.returnInvalidToken(response);
    }

    /**
     * 认证成功
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("认证成功! -- url: {}, token: {}", request.getRequestURI(), authentication.getCredentials());
//        response.sendRedirect(request.getRequestURI());
        setDefaultTargetUrl(request.getRequestURI());
        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("注销成功 -- token: {}", authentication.getCredentials());
    }


    /**
     * Session超时
     * @param event
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        log.warn("Session超时 -- {}", event);
        ResponseUtil.returnSessionOutOfTime(event.getResponse());
    }

}
