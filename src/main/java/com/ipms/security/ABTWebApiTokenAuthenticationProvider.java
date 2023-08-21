package com.ipms.security;

import com.ipms.sys.exception.InvalidTokenException;
import com.ipms.sys.model.dto.UserView;
import com.ipms.sys.service.UserService;
import com.ipms.sys.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
        log.debug("Parameter authentication instancetype : {}", authentication.getClass());
        log.info("Web api 认证用户 -- authentication: {}", authentication.toString());
        Assert.isInstanceOf(ABTWebApiAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("ABTWebApiTokenAuthenticationProvider.onlySupports",
                        "Only ABTWebApiAuthenticationToken is supported"));
        String tokenValue = authentication.getCredentials().toString();

        UserView user = (UserView) this.tokenUserDetailsService().loadUserByUsername(tokenValue);

        return new ABTWebApiAuthenticationToken(tokenValue);
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


    @Bean
    public UserDetailsService tokenUserDetailsService() {
        return token -> {
            log.info("tokenUserDetailsServiceImpl.loadUserByUsername(token), token={}", token);
            Optional<UserView> user = userService.userInfoBy(token);
            if (!user.isPresent()) {
                log.error("认证失败。未找到用户 by token: {}", token);
                throw new InvalidTokenException(this.messages.getMessage("ex.token.invalid.common"));
            }
            return (UserView) user.get();
        };
    }
}
