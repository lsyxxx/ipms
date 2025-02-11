package com.abt.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Category {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "DtCode", nullable = false, length = 50)
    private String dtCode;

    @Size(max = 50)
    @Column(name = "DtValue", length = 50)
    private String dtValue;

    @NotNull
    @Column(name = "Enable", nullable = false)
    private Boolean enable = false;

    @NotNull
    @Column(name = "SortNo", nullable = false)
    private Integer sortNo;

    @Size(max = 500)
    @Column(name = "Description", length = 500)
    private String description;

    @Size(max = 50)
    @Column(name = "TypeId", length = 50)
    private String typeId;

    @NotNull
    @Column(name = "CreateTime", nullable = false)
    private LocalDateTime createTime;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 200)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 200)
    private String createUserName;

    @Column(name = "UpdateTime")
    private LocalDateTime updateTime;

    @Size(max = 50)
    @Column(name = "UpdateUserId", length = 50)
    private String updateUserId;

    @Size(max = 200)
    @Column(name = "UpdateUserName", length = 200)
    private String updateUserName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TypeId", referencedColumnName = "Id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private CategoryType categoryType;

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dtCode='" + dtCode + '\'' +
                ", dtValue='" + dtValue + '\'' +
                ", enable=" + enable +
                ", sortNo=" + sortNo +
                ", description='" + description + '\'' +
                ", typeId='" + typeId + '\'' +
                ", createTime=" + createTime +
                ", createUserId='" + createUserId + '\'' +
                ", createUserName='" + createUserName + '\'' +
                ", updateTime=" + updateTime +
                ", updateUserId='" + updateUserId + '\'' +
                ", updateUserName='" + updateUserName + '\'' +
                ", categoryType=[" + categoryType.getName() + "]" +
                '}';
    }
}