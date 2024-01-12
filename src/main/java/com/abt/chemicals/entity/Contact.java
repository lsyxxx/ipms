package com.abt.chemicals.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * 联系方式
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
//@Table(name = "T_chm_contact")
//@Entity
//@EntityListeners(AuditingEntityListener.class)
public class Contact extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private List<String> phone;
    private String companyId;
    private String wechat;
    private String email;
    private int sort;
}
