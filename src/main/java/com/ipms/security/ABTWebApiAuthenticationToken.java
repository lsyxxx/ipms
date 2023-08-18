package com.ipms.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.util.Collection;

/**
 *  从webapi 验证的token
 *  参考 UsernamePasswordAuthenticationToken
 */
@Slf4j
public class ABTWebApiAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * request header key
     */
    private static final String REQ_HEADER_KEY = "X-Token";

    /**
     * 用户
     * 暂时用不上
     */
    private final Object principal;

    /**
     * x-token value
     */
    private Object credentials;

    //TODO: 可以扩展其它信息，比如可见菜单



    public ABTWebApiAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public ABTWebApiAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    public static ABTWebApiAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new ABTWebApiAuthenticationToken(principal, credentials);
    }

    public static ABTWebApiAuthenticationToken authenticate(Object principal, Object credentials,
                                                            Collection<? extends GrantedAuthority> authorities) {

        return new ABTWebApiAuthenticationToken(authorities, principal, credentials);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }


    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
