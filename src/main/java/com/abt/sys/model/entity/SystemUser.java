package com.abt.sys.model.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息表
 * 懒得改了就这样吧
 */
@Data
public class SystemUser {

    private String Account;
    private String Password;
    private String Name;
    private int Sex;
    private int Status;
    private String BizCode;
    private LocalDateTime CreateTime;
    private String CreateId;
    private String TypeName;
    private String TypeId;
    private String Tel;
    private String Tman;
    private String Levelpost;
    private String Papers;
    private String Mobile;
    private String IDCARD;
    private String Edu;
    private String Address;
    private String RZDay;
    private String Native;
    private String Thpapers;
    private String Spec;
    private String ZPImage;
    private String Birthday;
    private String Fnote;
    private String Jpost;
    private String Dman;
    private String FaAddress;
    private String Atel;
    private String UserSign;
    private String emailaddress;
    private String OrgFromId;
    private String orgFromIdName;
    private String bypapers;
    private String edupapers;
    private String idcardzhengmian;
    private String idcardfanmianpapers;
    private String idcardqitapapers;
    private String tpost;
    private String empnum;


}
