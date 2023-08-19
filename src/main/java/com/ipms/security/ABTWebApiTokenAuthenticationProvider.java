package com.ipms.security;

import com.ipms.sys.exception.InvalidTokenException;
import com.ipms.sys.model.dto.AuthUserDetails;
import com.ipms.sys.model.dto.UserView;
import com.ipms.sys.service.UserService;
import com.ipms.sys.service.impl.UserFromWebApiServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 *  通过request token 验证并获取用户信息
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ABTWebApiTokenAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Get User info from Web api -- authentication: {}", authentication.toString());
        //TODO: Handle


        return authentication;
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
            Optional user = userService.userInfoBy(token);
            if (!user.isPresent()) {
                throw new InvalidTokenException("Invalid token: " + token);
            }
            return (UserDetails) user.get();
        };
    }
}
