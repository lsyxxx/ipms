package com.abt.sys.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"User\"")
@Immutable
public class TUser {
    @Id
    @Column(name = "Id", columnDefinition = "PrimaryKey not null")
    private String id;

    @Size(max = 255)
    @NotNull
    @ColumnDefault("' '")
    @Column(name = "Account", nullable = false)
    private String account;

    @Size(max = 255)
    @NotNull
    @ColumnDefault("' '")
    @Column(name = "Password", nullable = false)
    private String password;

    @Size(max = 255)
    @NotNull
    @ColumnDefault("' '")
    @Column(name = "Name", nullable = false)
    private String name;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "Sex", nullable = false)
    private Integer sex;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "Status", nullable = false)
    private Integer status;

    @Size(max = 255)
    @ColumnDefault("' '")
    @Column(name = "BizCode")
    private String bizCode;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    private LocalDateTime createTime;

    @Size(max = 20)
    @Nationalized
    @Column(name = "TypeName", length = 20)
    private String typeName;

    @Size(max = 30)
    @Column(name = "Edu", length = 30)
    private String edu;

    @Size(max = 30)
    @Column(name = "Spec", length = 30)
    private String spec;

    @Column(name = "Birthday")
    private LocalDateTime birthday;

    @Column(name = "RZDay")
    private LocalDateTime rZDay;

    @Size(max = 20)
    @Column(name = "Native", length = 20)
    private String nativeField;

    @Size(max = 50)
    @Column(name = "Address", length = 50)
    private String address;

    @Size(max = 50)
    @Column(name = "FaAddress", length = 50)
    private String faAddress;

    @Size(max = 50)
    @Column(name = "Tel", length = 50)
    private String tel;

    @Size(max = 50)
    @Column(name = "Mobile", length = 50)
    private String mobile;

    @Size(max = 50)
    @Column(name = "Tman", length = 50)
    private String tman;

    @Size(max = 50)
    @Column(name = "Dman", length = 50)
    private String dman;

    @Size(max = 50)
    @Column(name = "Jpost", length = 50)
    private String jpost;

    @Size(max = 50)
    @Column(name = "Levelpost", length = 50)
    private String levelpost;

    @Size(max = 50)
    @Column(name = "IDCARD", length = 50)
    private String idcard;

    @Size(max = 50)
    @Column(name = "Fnote", length = 50)
    private String fnote;

    @Size(max = 15)
    @Column(name = "Atel", length = 15)
    private String atel;

    @Size(max = 200)
    @Column(name = "Thpapers", length = 200)
    private String thpapers;

    @Size(max = 200)
    @Column(name = "UserSign", length = 200)
    private String userSign;

    @Size(max = 200)
    @Column(name = "Papers", length = 200)
    private String papers;

    @Size(max = 200)
    @Column(name = "ZPImage", length = 200)
    private String zPImage;

    @Lob
    @Column(name = "OrgFromId")
    private String orgFromId;

    @Lob
    @Column(name = "orgFromIdName")
    private String orgFromIdName;

    @Size(max = 500)
    @Column(name = "bypapers", length = 500)
    private String bypapers;

    @Size(max = 500)
    @Column(name = "edupapers", length = 500)
    private String edupapers;

    @Size(max = 500)
    @Column(name = "idcardzhengmian", length = 500)
    private String idcardzhengmian;

    @Size(max = 500)
    @Column(name = "idcardfanmianpapers", length = 500)
    private String idcardfanmianpapers;

    @Size(max = 500)
    @Column(name = "idcardqitapapers", length = 500)
    private String idcardqitapapers;
    @Size(max = 50)
    @Column(name = "tpost", length = 50)
    private String tpost;

    @Size(max = 50)
    @Column(name = "emailaddress", length = 50)
    private String emailaddress;
    @Size(max = 50)
    @Column(name = "empnum", length = 50)
    private String empnum;

    @Column(name = "CreateId")
    private String createId;

    @Column(name = "TypeId")
    private String typeId;

}