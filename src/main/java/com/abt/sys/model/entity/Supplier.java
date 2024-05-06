package com.abt.sys.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_Supplier")
public class Supplier {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="name_", columnDefinition="VARCHAR(128)")
    private String name;
    @Column(name="contact_", columnDefinition="VARCHAR(32)")
    private String contact;

    @Column(name="tel_no", columnDefinition="VARCHAR(32)")
    private String telNo;
    @Column(name="address_", columnDefinition="VARCHAR(128)")
    private String address;
    @Column(name="bank_name", columnDefinition="VARCHAR(128)")
    private String bankName;
    @Column(name="bank_account", columnDefinition="VARCHAR(32)")
    private String bankAccount;
    @Column(name="tax_no", columnDefinition="VARCHAR(32)")
    private String taxNo;
    @Column(name="type_", columnDefinition="VARCHAR(16)")
    private String type;



}