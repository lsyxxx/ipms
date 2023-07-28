package com.ipms.sys.model.dto;

import com.ipms.sys.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 已加载用户
 */
@Slf4j
public class AuthUserDetails implements UserDetails {

    private final User user;
    private static final List<GrantedAuthority> ROLES = Collections
            .unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));

    public AuthUserDetails(User user) {
        this.user = user;
    }

    /**
     * 用户权限
     * TODO
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ROLES;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * 用户限制功能-账户过期
     * 不需要设置限制就返回true
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户限制功能-账户锁定
     * 不需要设置限制就返回true
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户限制功能-凭据过期
     * 不需要设置限制就返回true
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户限制功能-禁用账户
     * 不需要设置限制就返回true
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
