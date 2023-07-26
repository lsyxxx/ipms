package com.ipms.security;

import com.ipms.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@Slf4j
public class UserLoginAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    public UserLoginAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("UserLoginAuthenticationProvider.authenticate - name: {}", authentication.getName() );
        //验证通过
        UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
        log.debug(userDetails.toString());

        //验证不通过
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
