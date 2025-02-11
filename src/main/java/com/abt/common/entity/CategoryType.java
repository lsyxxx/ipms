package com.abt.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class CategoryType {
    @Id
    @Column(name = "Id", columnDefinition = "PrimaryKey not null")
    private Object id;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @ColumnDefault("' '")
    @Column(name = "Name", nullable = false)
    private String name;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "categoryType")
    private List<Category> categoryList;

    @Override
    public String toString() {
        return "CategoryType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", categoryList=" + categoryList +
                '}';
    }
}