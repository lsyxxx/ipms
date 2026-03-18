package com.abt.wxapp.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 微信小程序用户的 Spring Security 主体
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxUserDetails implements UserDetails {

    private final String userId;

    /** 微信 openid，作为唯一标识 */
    private final String openid;

    /** 昵称 / 显示名称 */
    private final String username;

    //TODO: 用户权限授权
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /** 小程序不使用密码登录 */
    @Override
    public String getPassword() {
        return null;
    }

    /** 以 openid 作为用户名 */
    @Override
    public String getUsername() {
        return openid;
    }

    /**
     * 账号登录过期
     * @return 是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    /**
     * 凭证（密码）是否未过期，是否强制修改密码
     * @return 是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
