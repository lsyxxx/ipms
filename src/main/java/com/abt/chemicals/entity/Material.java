package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 化学品材料
 */
@Data
@Table(name = "chm_material")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    /**
     * main/aux
     * 主要/辅助
     */
    private String type;
    private String chemicalId;
}
