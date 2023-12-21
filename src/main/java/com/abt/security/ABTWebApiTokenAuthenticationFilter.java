package com.abt.security;

import com.abt.common.util.ResponseUtil;
import com.abt.http.dto.WebApiToken;
import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.model.dto.UserView;
import com.abt.common.util.MessageUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  在SpringSecurity FilterChain 添加一个Filter
 *  从request中获取token，调用webapi 获取用户信息
 */

@Slf4j
public class ABTWebApiTokenAuthenticationFilter extends OncePerRequestFilter {
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    private final AuthenticationManager authenticationManager;
    private final TokenAuthenticationHandler tokenAuthenticationHandler;

    /**
     * Request header key
     */
    private final WebApiToken tokenConfig;
    private OrRequestMatcher ignore = createOrMatcher();

    public ABTWebApiTokenAuthenticationFilter(AuthenticationManager authenticationManager, TokenAuthenticationHandler tokenAuthenticationHandler, WebApiToken tokenConfig) {
        this.authenticationManager = authenticationManager;
        this.tokenAuthenticationHandler = tokenAuthenticationHandler;
        this.tokenConfig = tokenConfig;
    }


    public String obtainTokenValue(HttpServletRequest request) {
        return request.getHeader(tokenConfig.getTokenKey());
    }

    private static OrRequestMatcher createOrMatcher() {
        List<RequestMatcher> matchers = new ArrayList<>(SecurityConfig.whiteList().length);
        for (String pattern : SecurityConfig.whiteList()) {
            matchers.add(new AntPathRequestMatcher(pattern));
        }
        return new OrRequestMatcher(matchers);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("拦截 URL: {}, 开始获取用户token认证原始凭证", request.getRequestURI());

        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            return;
        }

        //不进行认证
        if (ignore.matches(request)) {
            log.info("不需要认证url: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String tokenValue = obtainTokenValue(request);
            if (tokenValue == null) {
                log.error("Token is null!");
                this.tokenAuthenticationHandler.onAuthenticationFailure(request, response, new InvalidTokenException(this.messages.getMessage("ex.token.invalid.common")));
                return;
            }
            tokenValue = tokenValue.trim();
            log.info("Token value : {}", tokenValue);
            //生成需要认证的token
            ABTWebApiAuthenticationToken authRequest = ABTWebApiAuthenticationToken.unauthenticated(tokenValue);
            //认证token
            Authentication authedToken =  this.authenticationManager.authenticate(authRequest);
            //authedToken  没有credentials
            this.successfulAuthentication(request, authedToken);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            this.tokenAuthenticationHandler.onAuthenticationFailure(request, response, authenticationException);
        } catch (Exception ex) {
            log.error("ABTWebApiTokenAuthenticationFilter 认证异常 - {} , {}", ex.getMessage(), ex.getLocalizedMessage());
            ResponseUtil.returnFail(response, ex.getMessage());
        }




    }

    protected void successfulAuthentication(HttpServletRequest request, Authentication authResult) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            //authedToken  没有credentials?
            ABTWebApiAuthenticationToken authToken = new ABTWebApiAuthenticationToken(authResult.getAuthorities(), (UserView) authResult.getPrincipal(), obtainTokenValue(request));
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("ABTWebApiAuthenticationToken 保存于 SecurityContextHolder");
        }
    }

}
