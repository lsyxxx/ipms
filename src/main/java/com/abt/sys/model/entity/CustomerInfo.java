package com.abt.sys.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_Customer_Info")
@Accessors(chain = true)
public class CustomerInfo {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
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
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
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


    @Override
    public String toString() {
        return "CustomerInfo{" +
                "id='" + id + '\'' +
                ", customerid='" + customerid + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerClass='" + customerClass + '\'' +
                '}';
    }
}