package com.abt.chemicals.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 报价
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name = "chm_price")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Price extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private double price;

    @Column(name="unit_", columnDefinition="VARCHAR(32)")
    private String unit;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    /**
     * 公司Id
     */
    @Column(name="com_id", columnDefinition="VARCHAR(128)")
    private String companyId;
    /**
     * 化学品id
     */
    @Column(name="chm_id", columnDefinition="VARCHAR(128)")
    private String chemicalId;

}
