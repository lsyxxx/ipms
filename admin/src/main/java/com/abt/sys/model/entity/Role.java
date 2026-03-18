package com.abt.sys.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role implements GrantedAuthority {
    @Id
    @Column(name = "Id", columnDefinition = "PrimaryKey not null")
    private String id;

    @Size(max = 255)
    @NotNull
    @ColumnDefault("' '")
    @Column(name = "Name", nullable = false)
    private String name;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "Status", nullable = false)
    private Integer status;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    private LocalDateTime createTime;

    @Size(max = 20)
    @Nationalized
    @Column(name = "TypeName", length = 20)
    private String typeName;

    @Column(name = "CreateId", columnDefinition = "PrimaryKey")
    private String createId;


    @Column(name = "TypeId", columnDefinition = "PrimaryKey")
    private String typeId;


    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", typeName='" + typeName + '\'' +
                ", createId='" + createId + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }

    @Override
    public String getAuthority() {
        return this.id;
    }
}