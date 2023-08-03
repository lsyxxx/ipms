package com.ipms.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 从数据库读取权限
 */
@Component
@Slf4j
public class DatabaseAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {


    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
        log.info("DatabaseAuthorizationManager verify()...");
    }

    @Nullable
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        log.info("DatabaseAuthorizationManager check() { load from database }");
        log.debug("RequestAuthorizationContext object: {}", object);
        log.debug("Supplier<Authentication> authentication: {}", authentication);
        return null;
    }
}
