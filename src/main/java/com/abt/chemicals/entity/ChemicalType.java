package com.abt.chemicals.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 化学品分类
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name = "chm_type")
@DynamicInsert
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ChemicalType extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name="name_", columnDefinition="VARCHAR(128)", nullable = false)
    private String name;
    @Column(name="note", columnDefinition="VARCHAR(1000)")
    private String note;
    @Column(name="parent_id", columnDefinition="VARCHAR(128)")
    private String parentId;
    /**
     * 序号
     */
    @Column(name="sort_", columnDefinition="TINYINT")
    private int sort = 0;
    /**
     * 层级
     * 1/2
     */
    @Column(name="level_", columnDefinition="TINYINT")
    private int level;

    @Column(name="enable_", columnDefinition="BIT")
    private boolean enable;

    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;


    @Transient
    private List<ChemicalType> children = new ArrayList<>();
    @Transient
    private ChemicalType parent;

    public void setParent(ChemicalType type) {
        this.parent = new ChemicalType();
        parent.setId(type.getId());
        parent.setName(type.getName());
        parent.setSort(type.getSort());
        parent.setLevel(type.getLevel());
        parent.setNote(type.getNote());
        parent.setEnable(type.isEnable());
    }

}
