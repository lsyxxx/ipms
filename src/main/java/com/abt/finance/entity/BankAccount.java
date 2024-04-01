package com.abt.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 银行账户
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "fi_bank_acct")
@DynamicInsert
@DynamicUpdate
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 银行账号
     */
    @NotNull
    @Column(name = "account_", columnDefinition="VARCHAR(128)")
    private String account;

    /**
     * 开户行
     */
    @NotNull
    @Column(name="bank_", columnDefinition="VARCHAR(256)")
    private String bank;
    /**
     * 账户类型
     */
    @Column(name="type_", columnDefinition="VARCHAR(128)")
    private String type;
    /**
     * 账户所属公司
     */
    @NotNull
    @Column(name="company_", columnDefinition="VARCHAR(128)")
    private String company;
    /**
     * 开户行地址
     */
    @Column(name="address_", columnDefinition="VARCHAR(512)")
    private String address;


}
