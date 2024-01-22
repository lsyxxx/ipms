package com.abt.chemicals.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 联系方式
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name = "chm_contact")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Contact extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name="name_", columnDefinition="VARCHAR(128)")
    private String name;
    /**
     * 号码1
     * 不做过多限制
     */
    @Column(columnDefinition="VARCHAR(32)")
    private String tel1;
    /**
     * 号码2
     */
    @Column(columnDefinition="VARCHAR(32)")
    private String tel2;
    private String wechat;
    private String email;
    private int sort;
    @Column(name="note", columnDefinition="VARCHAR(1000)")
    private String note;
    @Column(name = "com_id", columnDefinition="VARCHAR(128)")
    private String companyId;
    @Column(name="chm_id", columnDefinition="VARCHAR(128)")
    private String chemicalId;
}
