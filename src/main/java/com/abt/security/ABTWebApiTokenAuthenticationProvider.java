package com.abt.security;

import com.abt.http.dto.WebApiToken;
import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.common.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 *  通过request token 验证并获取用户信息
 */

@Slf4j
//@Component
@Configuration
@RequiredArgsConstructor
public class ABTWebApiTokenAuthenticationProvider implements AuthenticationProvider, AuthenticationManager {

    private final UserService userService;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Web api 正在认证用户 -- authentication: {}", authentication.toString());
        Assert.isInstanceOf(ABTWebApiAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("ABTWebApiTokenAuthenticationProvider.onlySupports",
                        "Only ABTWebApiAuthenticationToken is supported"));
        String tokenValue = authentication.getCredentials().toString();

        Optional<UserView> user = userService.userInfoBy(WebApiToken.of(authentication));
        //authorization, 获取权限
        Collection authorities = Collections.unmodifiableList(new ArrayList<>());
        log.warn("TODO: 获取用户权限 -- authorities: {}", authorities);
        log.info("认证用户 -- {}:{} 成功", user.get().getId(), user.get().getName());
        return ABTWebApiAuthenticationToken.authenticate(user.get(), tokenValue, authorities);
    }


    /**
     * TODO: 只对ABTWebApiAuthenticationToken 进行处理
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (ABTWebApiAuthenticationToken.class.isAssignableFrom(authentication));
    }


}
