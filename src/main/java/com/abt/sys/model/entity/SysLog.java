package com.abt.sys.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name="SysLog")
public class SysLog {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 1000)
    @Nationalized
    @Column(name = "Content", length = 1000)
    private String content;

    @Size(max = 20)
    @Nationalized
    @Column(name = "TypeName", length = 20)
    private String typeName;

    @Size(max = 200)
    @Column(name = "Href", length = 200)
    private String href;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    @CreatedDate
    private LocalDateTime createTime;

    @Size(max = 200)
    @Nationalized
    @Column(name = "CreateName", length = 200)
    private String createName;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "Result", nullable = false)
    private Integer result = 0;

    @Size(max = 20)
    @Column(name = "Ip", length = 20)
    private String ip;
    @Size(max = 50)
    @Nationalized
    @Column(name = "Application", length = 50)
    private String application;

    @Column(name = "TypeId")
    private String typeId;

    @CreatedBy
    @Column(name = "CreateId")
    private String createId;
}