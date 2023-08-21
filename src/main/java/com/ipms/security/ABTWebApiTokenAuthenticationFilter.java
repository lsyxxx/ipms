package com.ipms.security;

import com.ipms.sys.exception.InvalidTokenException;
import com.ipms.sys.util.MessageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 *  在SpringSecurity FilterChain 添加一个Filter
 * 从request中获取token，调用webapi 获取用户信息
 */

@Slf4j
public class ABTWebApiTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    /**
     * Request header key
     */
    public static final String ABT_TOKEN_KEY = "X-Token";

    protected ABTWebApiTokenAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("拦截 URL: {}, 开始获取用户token认证原始凭证", request.getRequestURI());
        String tokenValue = obtainTokenValue(request);
        if (tokenValue == null) {
            log.error("Token is null!");
            throw new InvalidTokenException(this.messages.getMessage("ex.token.invalid.common"));
        }
//        Assert.notNull(tokenValue, this.messages.getMessage("ABTWebApiTokenAuthenticationFilter.nullToken"));
        tokenValue = tokenValue.trim();
        log.info("Token value : {}", tokenValue);
        ABTWebApiAuthenticationToken authRequest = ABTWebApiAuthenticationToken.unauthenticated(tokenValue);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public String obtainTokenValue(HttpServletRequest request) {
        return request.getParameter(ABT_TOKEN_KEY);
    }

    protected void setDetails(HttpServletRequest request, ABTWebApiAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }



}
