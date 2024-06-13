package com.abt.sys.model.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.service.impl.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_Customer_Info")
@Accessors(chain = true)
@EntityListeners({AuditingEntityListener.class, CommonJpaAuditListener.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerInfo implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 50)
    @Column(name = "Customerid", length = 50)
    private String customerid;

    @Size(max = 100)
    @Column(name = "CustomerName", length = 100)
    private String customerName;

    @Size(max = 200)
    @Column(name = "CustomerAddress", length = 200)
    private String customerAddress;

    @Size(max = 100)
    @Column(name = "CustomerClass", length = 100)
    private String customerClass;

    @Size(max = 50)
    @Column(name = "Email", length = 50)
    private String email;

    @Size(max = 50)
    @Column(name = "WeiXinNumber", length = 50)
    private String weiXinNumber;

    @Size(max = 50)
    @Column(name = "BankAccount", length = 50)
    private String bankAccount;

    @Size(max = 50)
    @Column(name = "DepositBank", length = 50)
    private String depositBank;

    @Size(max = 20)
    @Column(name = "Contact1", length = 20)
    private String contact1;

    @Size(max = 20)
    @Column(name = "Telephone1", length = 20)
    private String telephone1;

    @Size(max = 20)
    @Column(name = "Contact2", length = 20)
    private String contact2;

    @Size(max = 20)
    @Column(name = "Telephone2", length = 20)
    private String telephone2;

    @Size(max = 2)
    @Column(name = "ContractCustomer", length = 2)
    private String contractCustomer;

    @Size(max = 10)
    @Column(name = "SalesRepresentative", length = 10)
    private String salesRepresentative;

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
    @Column(name = "CreateDate", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    @LastModifiedBy
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Lob
    @Column(name = "FuJianImage")
    private String fuJianImage;

    @Size(max = 4)
    @Column(name = "IsAvtive", length = 4)
    private String isAvtive;

    /**
     * 纳税人识别号
     */
    @Size(max = 50)
    @Column(name = "TaxNo", length = 50)
    private String taxNo;


    @Override
    public String toString() {
        return "CustomerInfo{" +
                "id='" + id + '\'' +
                ", customerid='" + customerid + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerClass='" + customerClass + '\'' +
                '}';
    }

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