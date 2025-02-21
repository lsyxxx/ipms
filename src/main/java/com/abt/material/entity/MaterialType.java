package com.abt.material.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 物品分类
 */

@Getter
@Setter
@Entity
@Table(name = "T_stockcatalog")
public class MaterialType {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 名称
     */
    @Size(max = 50)
    @NotNull
    @Column(name = "Fname", nullable = false, length = 50)
    private String name;

    /**
     * 序号
     */
    @Column(name = "XH")
    private Integer index;

    /**
     * 是否删除
     */
    @Column(name="IsDel", columnDefinition = "BIT")
    private boolean isDeleted = false;

}