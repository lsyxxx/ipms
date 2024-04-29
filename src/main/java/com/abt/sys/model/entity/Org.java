package com.abt.sys.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Org")
public class Org {
    @Id
    @Column(name = "Id", nullable = false)
    private String id;

    @Size(max = 255)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "CascadeId", nullable = false)
    private String cascadeId;

    @Size(max = 255)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "Name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "HotKey", nullable = false)
    private String hotKey;

    @Size(max = 255)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "ParentName", nullable = false)
    private String parentName;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "IsLeaf", nullable = false)
    private Boolean isLeaf = false;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "IsAutoExpand", nullable = false)
    private Boolean isAutoExpand = false;

    @Size(max = 255)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "IconName", nullable = false)
    private String iconName;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "Status", nullable = false)
    private Integer status;

    @Size(max = 255)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "BizCode", nullable = false)
    private String bizCode;

    @Size(max = 4000)
    @NotNull
    @ColumnDefault(" ")
    @Column(name = "CustomCode", nullable = false, length = 4000)
    private String customCode;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    private LocalDateTime createTime;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "CreateId", nullable = false)
    private Integer createId;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SortNo", nullable = false)
    private Integer sortNo;

    @Column(name = "ParentId", columnDefinition = "PrimaryKey")
    private String parentId;

    @Size(max = 20)
    @Nationalized
    @Column(name = "TypeName", length = 20)
    private String typeName;

    @Column(name = "TypeId", columnDefinition = "PrimaryKey")
    private String typeId;
}