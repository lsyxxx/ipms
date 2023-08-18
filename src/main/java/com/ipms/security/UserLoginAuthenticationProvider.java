package com.ipms.security;

import com.ipms.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * 自定义的用户名密码登录
 */
@Slf4j
public class UserLoginAuthenticationProvider {

    private final UserService userService;

    public UserLoginAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

//    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("UserLoginAuthenticationProvider.authenticate - name: {}", authentication.getName() );
        //验证通过
        UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
        log.debug(userDetails.toString());

        //校验

        //TOKEN
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());
        token.setDetails(userDetails);
        //TODO: 验证不通过
        return token;
    }

//    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
