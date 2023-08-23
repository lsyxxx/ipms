package com.abt.security;

import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.service.PermissionService;
import com.abt.sys.service.UserService;
import com.abt.common.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 授权组件
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ABTAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    private final PermissionService permissionService;
    private final UserService userService;

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        log.info("执行ABTAuthorizationManager.verify()...");
        AuthorizationManager.super.verify(authentication, object);
    }

    @Nullable
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        log.info("执行ABTAuthorizationManager.check()...authentication: {}", authentication);

        // 当前用户 或 系统权限信息 是空数据的时候，或者是有数据并且和当前用户权限匹配的情况下返回true
        boolean isGranted = false;
        // request
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        // 请求URI
        String requestUri = request.getRequestURI();

        //TODO: Get userinfo/permissions
        String token = userService.getToken(request);
        List permissions = permissionService.getPermissionsBy(token);
        Optional user = userService.userInfoBy(token);
        if (!user.isPresent()) {
            //TODO: 用户不存在, 提示认证失败
            isGranted = false;
            throw new InvalidTokenException(messages.getMessage("ex.token.invalid.common"));
        }
        if (isAuthorizationUri(requestUri, permissions)) {
            isGranted = true;
        }

        //spring security authorization
        Collection<? extends GrantedAuthority> authorities = convert(permissions);



        //未授权
        isGranted = true;
        return new AuthorizationDecision(isGranted);
    }

    /**
     * TODO: 当前Url是否具有权限
     * @param uri
     * @return
     */
    private boolean isAuthorizationUri(String uri, List permissions) {
        return true;
    }

    /**
     * 将数据库中的权限转为GrantedAuthority
     * @param permissions
     * @return
     */
    private List<? extends GrantedAuthority> convert(List permissions) {
        return List.of();
    }
}
