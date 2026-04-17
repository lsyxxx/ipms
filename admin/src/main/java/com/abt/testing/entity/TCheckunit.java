package com.abt.testing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "T_checkunit")
public class TCheckunit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "Fid", nullable = false, length = 50)
    private String fid;

    @Size(max = 50)
    @NotNull
    @Column(name = "Fname", nullable = false, length = 50)
    private String fname;

    @Size(max = 500)
    @Column(name = "Note", length = 500)
    private String note;

    @Size(max = 2)
    @Column(name = "IsActive", length = 2)
    private String isActive;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private Instant createDate;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private Instant operatedate;

    @Size(max = 2)
    @Column(name = "delFlag", length = 2)
    private String delFlag;


}