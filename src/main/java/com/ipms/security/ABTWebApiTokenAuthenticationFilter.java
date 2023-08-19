package com.ipms.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *  在SpringSecurity FilterChain 添加一个Filter
 * 从request中获取token，调用webapi 获取用户信息
 */

@Slf4j
@Component
public class ABTWebApiTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private static final String MATCH_ALL = "/**";

    /**
     * Request header key
     */
    public static final String ABT_TOKEN_KEY = "X-Token";

    private String tokenKey = ABT_TOKEN_KEY;

    /**
     * URL必须token验证
     */
    protected ABTWebApiTokenAuthenticationFilter() {
        super(new AntPathRequestMatcher(MATCH_ALL));

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("Authenticate token....");
        String tokenValue = obtainTokenValue(request);
        tokenValue = (tokenValue != null) ? tokenValue.trim() : "";
        log.info("Token value : {}", tokenValue);
        ABTWebApiAuthenticationToken authRequest = ABTWebApiAuthenticationToken.unauthenticated(null, tokenValue);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public String obtainTokenValue(HttpServletRequest request) {
        return request.getParameter(tokenKey);
    }

    protected void setDetails(HttpServletRequest request, ABTWebApiAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}
