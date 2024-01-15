package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 联系方式
 */
@Data
@Table(name = "chm_contact")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    /**
     * 号码1
     * 不做过多限制
     */
    private String tel1;
    /**
     * 号码2
     */
    private String tel2;
    private String companyId;
    private String wechat;
    private String email;
    private int sort;
    private String note;
}
