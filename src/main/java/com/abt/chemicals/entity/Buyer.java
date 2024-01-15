package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * 采购商
 */
@Data
@Table(name = "chm_buyer")
@Entity
@EntityListeners(AuditingEntityListener.class)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Buyer extends Company{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Transient
    private List<Price> priceList;

    @Transient
    private List<Contact> contactList;



}
