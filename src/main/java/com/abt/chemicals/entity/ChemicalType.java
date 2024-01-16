package com.abt.chemicals.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 化学品分类
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "chm_type")
@DynamicInsert
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ChemicalType extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name="name_", columnDefinition="VARCHAR(128)")
    private String name;
    private String note;
    private String parentId;
    /**
     * 序号
     */
    @Column(name="sort_", columnDefinition="TINYINT")
    private int sort;
    /**
     * 层级
     */
    @Column(name="level_")
    private int level;
}
