package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 化学品分类
 */
@Data
@Table(name = "chm_type")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ChemicalType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;
    private String parentId;
    /**
     * 排序
     */
    private int sort;
    /**
     * 层级
     */
    private int level;
}
