package com.abt.sys.model.entity;

import com.abt.common.service.impl.CommonJpaAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_Supply_Info")
public class SupplyInfo implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "Supplierid", nullable = false, length = 50)
    private String supplierid;

    @Size(max = 200)
    @Column(name = "SupplierName", length = 200)
    private String supplierName;

    @Size(max = 500)
    @Column(name = "SupplierAddress", length = 500)
    private String supplierAddress;

    @Size(max = 200)
    @Column(name = "SupplierGoodsName", length = 200)
    private String supplierGoodsName;

    @Size(max = 50)
    @Column(name = "AccountName", length = 50)
    private String accountName;

    @Size(max = 50)
    @Column(name = "BankAccount", length = 50)
    private String bankAccount;

    @Size(max = 50)
    @Column(name = "DepositBank", length = 50)
    private String depositBank;

    @Size(max = 20)
    @Column(name = "Contact", length = 20)
    private String contact;

    @Size(max = 20)
    @Column(name = "Telephone", length = 20)
    private String telephone;

    @Column(name = "EvaluateTime", columnDefinition = "DATETIME")
    private LocalDateTime evaluateTime;

    @Size(max = 200)
    @Column(name = "EvaluateContent", length = 200)
    private String evaluateContent;

    @Size(max = 10)
    @Column(name = "EvaluateResults", length = 10)
    private String evaluateResults;

    @Size(max = 100)
    @Column(name = "Causeofnonconformity", length = 100)
    private String causeOfNonconformity;

    @Size(max = 200)
    @Column(name = "Note", length = 200)
    private String note;

    @Lob
    @Column(name = "FuJianFile")
    private String fuJianFile;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    @CreatedBy
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false, columnDefinition = "DATETIME")
    @CreatedDate
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    @LastModifiedBy
    private String operator;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @NotNull
    @Column(name = "Operatedate", nullable = false, columnDefinition = "DATETIME")
    @LastModifiedDate
    private LocalDateTime operateDate;

    @Size(max = 4)
    @Column(name = "IsAvtive", length = 4)
    private String isAvtive;

    @Override
    public String getCreateUserid() {
        return this.createUserId;
    }

    @Override
    public void setCreateUsername(String username) {
        this.createUserName = username;
    }

    @Override
    public String getUpdateUserid() {
        return this.operator;
    }

    @Override
    public void setUpdateUsername(String username) {
        this.operatorName = username;
    }
}