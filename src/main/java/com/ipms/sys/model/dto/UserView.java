package com.ipms.sys.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  webapi 传来的 UserView
 *  属性名称来自UserView
 *
 *  增加认证授权信息
 */


@Data
@Accessors(chain = true)
public class UserView implements UserDetails{
    private String emailAddress;
    private String empnum;
    private String tpost;
    private String orgFromId;
    private String orgFromIdName;
    private String id;
    private String account;
    private String name;
    private String atel;
    private String userSign;
    /**
     * 原始数据似乎没有保存
     */
    private int sex;
    private int status;
    private int type;
    private LocalDateTime createTime;
    private String createUser;
    private String organizations;
    private String organizationIds;
    private String password;
    private String tel;
    private String tman;
    private String levelPost;
    private String papers;
    private String mobile;
    private String idCard;
    private String edu;
    private String address;
    private LocalDateTime rzDay;
    /**
     * native --> nativeStr
     * 关键字
     */
    private String nativeStr;
    private String bypapers;
    private String edupapers;
    private String idcardzhengmian;
    private String idcardfanmianpapers;
    private String thpapers;
    private String spec;
    private String zpImage;
    private LocalDateTime birthday;
    private String fnote;
    private String jpost;
    private String dman;
    private String faAddress;

    /**
     * TODO: 授权信息
     */
    private Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    /**
     * 用户过期true：没有过期  false：过期
     */
    private Boolean userIsAccountNonExpired;
    /**
     * 用户锁定true：没有锁定  false：被锁定
     */
    private Boolean userIsAccountNonLocked;

    private Boolean userIsEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return userIsAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userIsAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userIsEnabled;
    }
}
