package com.abt.security;

import com.abt.http.dto.WebApiToken;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.PermissionService;
import com.abt.sys.service.UserService;
import com.abt.common.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import com.abt.sys.model.entity.Role;

import java.util.*;

import static com.abt.sys.config.Constant.SYSTEM_ID;

/**
 *  通过request token 验证并获取用户信息
 */

@Slf4j
//@Component
@Configuration
@RequiredArgsConstructor
public class ABTWebApiTokenAuthenticationProvider implements AuthenticationProvider, AuthenticationManager {

    private final UserService userService;
    private final PermissionService permissionService;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Web api 正在认证用户 -- authentication: {}", authentication.toString());
        Assert.isInstanceOf(ABTWebApiAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("ABTWebApiTokenAuthenticationProvider.onlySupports",
                        "Only ABTWebApiAuthenticationToken is supported"));
        String tokenValue = authentication.getCredentials().toString();

        Optional<UserView> user = userService.userInfoBy(WebApiToken.of(authentication));
        List<Role> authorities = new ArrayList<>();
        if (user.isPresent()) {
            UserView authUser = user.get();
            Set<Role> roles;
            if (authUser.getId().equals(SYSTEM_ID)) {
                //超级管理员所有权限
                roles = permissionService.getAllRoles();
            } else {
                roles  = permissionService.getRolesByUserid(authUser.getId());
            }
            authorities.addAll(roles);
            authUser.setAuthorities(roles);

            log.info("认证用户 -- {}:{} 成功", authUser.getId(), authUser.getName());
            return ABTWebApiAuthenticationToken.authenticate(user.get(), tokenValue, authorities);
        }

        throw new BusinessException("非系统用户!请联系管理员");
    }


    /**
     * TODO: 只对ABTWebApiAuthenticationToken 进行处理
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (ABTWebApiAuthenticationToken.class.isAssignableFrom(authentication));
    }


}
