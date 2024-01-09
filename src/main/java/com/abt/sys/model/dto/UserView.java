package com.abt.sys.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
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
    @JsonProperty("emailaddress")
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
    private String typeName;
    private String typeId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String createId;
    private String createUser;
    private String organizations;
    private String organizationIds;
    private String password;
    private String tel;
    private String tman;
    @JsonProperty("levelpost")
    private String levelPost;
    private String papers;
    private String mobile;
    @JsonProperty("idcard")
    private String idCard;
    private String edu;
    private String address;
    private LocalDateTime rzDay;
    /**
     * native --> nativeStr
     * 关键字
     */
    @JsonProperty("native")
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
    private String bizCode;


    /**
     * TODO: 授权信息
     */
    @JsonIgnore
    private Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    /**
     * 用户过期true：没有过期  false：过期
     */
    @JsonIgnore
    private Boolean userIsAccountNonExpired;
    /**
     * 用户锁定true：没有锁定  false：被锁定
     */
    @JsonIgnore
    private Boolean userIsAccountNonLocked;

    @JsonIgnore
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

    public String simpleInfo() {
        return "[" + this.id + ", " + this.name + "]";
    }

}
