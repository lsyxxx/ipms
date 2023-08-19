package com.ipms.sys.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *  webapi 传来的 UserView
 *  属性名称来自UserView
 *  仅把首字母改成小写符合java
 */


@Data
public class UserView {
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

}
