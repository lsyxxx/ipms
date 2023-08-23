package com.abt.security;

import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.model.dto.UserView;
import com.abt.common.util.MessageUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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

    /**
     * Request header key
     */
    public static final String ABT_TOKEN_KEY = "X-Token";
    private OrRequestMatcher ignore = createOrMatcher();

    public ABTWebApiTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public String obtainTokenValue(HttpServletRequest request) {
        return request.getHeader(ABT_TOKEN_KEY);
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

        //不进行认证
        if (ignore.matches(request)) {
            log.info("不需要认证url: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String tokenValue = obtainTokenValue(request);
        if (tokenValue == null) {
            log.error("Token is null!");
            throw new InvalidTokenException(this.messages.getMessage("ex.token.invalid.common"));
        }
        tokenValue = tokenValue.trim();
        log.info("Token value : {}", tokenValue);
        ABTWebApiAuthenticationToken authRequest = ABTWebApiAuthenticationToken.unauthenticated(tokenValue);

        this.authenticationManager.authenticate(authRequest);


        this.successfulAuthentication(request, authRequest);

        filterChain.doFilter(request, response);

    }

    protected void successfulAuthentication(HttpServletRequest request, Authentication authResult) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            ABTWebApiAuthenticationToken authToken = new ABTWebApiAuthenticationToken(authResult.getAuthorities(), (UserView) authResult.getPrincipal(), (String)authResult.getPrincipal());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("ABTWebApiAuthenticationToken 保存于 SecurityContextHolder");
        }
    }

}
