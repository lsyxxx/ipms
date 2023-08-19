package com.ipms.sys.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 已加载用户
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class AuthUserDetails<T> implements UserDetails {

    private final T user;

    private final String password;

    private final String username;

    private Collection<? extends GrantedAuthority> roles = Collections.emptyList();




    public void setRoles(Collection<? extends GrantedAuthority> roles) {
        this.roles = roles;
    }


    /**
     * 用户权限
     * TODO
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
