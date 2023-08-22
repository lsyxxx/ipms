package com.ipms.security;

import com.ipms.sys.model.dto.UserView;
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
     * 用户，认证后保存用户原始信息UserView
     * 未认证则null
     */
    private final UserView principal;

    /**
     * x-token value
     */
    private String credentials;

    //TODO: 可以扩展其它信息，比如可见菜单


    public ABTWebApiAuthenticationToken(String token) {
        super(null);
        this.principal = null;
        this.credentials = token;
        super.setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public ABTWebApiAuthenticationToken(Collection<? extends GrantedAuthority> authorities, UserView user, String token) {
        super(authorities);
        this.principal = user;
        this.credentials = token;
        super.setAuthenticated(true); // must use super, as we override
    }

    /**
     * 未认证token
     * @param credentials: token
     * @return
     */
    public static ABTWebApiAuthenticationToken unauthenticated(String credentials) {
        return new ABTWebApiAuthenticationToken(credentials);
    }


    /**
     * 认证token
     * @param principal: 用户原始信息 UserView
     * @param credentials: token
     * @param authorities: 权限信息
     * @return
     */
    public static ABTWebApiAuthenticationToken authenticate(UserView principal, String credentials,
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
