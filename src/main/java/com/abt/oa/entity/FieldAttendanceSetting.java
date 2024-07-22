package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 野外考勤设置
 */
@Table(name = "field_atd_setting")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({AuditingEntityListener.class, CommonJpaAuditListener.class})
public class FieldAttendanceSetting extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="sort", columnDefinition="TINYINT")
    private int sort;

    @Column(name="name", columnDefinition="VARCHAR(128)")
    private String name;

    @Column(name="amount", columnDefinition="DECIMAL(9,2)")
    private double amount;

    @Column(name="desc", columnDefinition="VARCHAR(1000)")
    private String desc;

    @Column(name="group", columnDefinition="VARCHAR(128)")
    private String group;

    /**
     * 是否启用
     */
    @Column(name="enabled", columnDefinition="BIT")
    private boolean enabled = true;

}
