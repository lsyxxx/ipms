package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * 生产商
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "chm_producer")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Producer extends Company{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 化学品型号
     */
    private String code;

    @Transient
    private List<Contact> contactList;
    @Transient
    private List<Price> priceList;

}
