package com.abt.testing.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.service.impl.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


/**
 * 枚举类
 */
@Getter
@Setter
@Entity
@Table(name = "T_ENUMLIB")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumLib {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 枚举项
     */
    @Size(max = 50)
    @Column(name = "FID", length = 50)
    private String fid;


    /**
     * 分类id
     */
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "FTYPEID", nullable = false, length = 50)
    private String ftypeid;

    /**
     * 名称
     */
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "FNAME", nullable = false, length = 50)
    private String fname;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "FDESC", nullable = false, length = 50)
    private String fdesc;

    @Size(max = 18)
    @Nationalized
    @Column(name = "OPERATOR", length = 18)
    private String operator;

    @Column(name = "OPERATEDATE", columnDefinition = "DATETIME")
    private LocalDateTime operateDate;

    @Size(max = 13)
    @Nationalized
    @Column(name = "OPERATEDEPT", length = 13)
    private String operateDept;

}