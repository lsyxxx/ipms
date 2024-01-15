package com.abt.chemicals.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * 报价
 */
@Data
@Table(name = "chm_price")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private double price;
    private LocalDate date;
    /**
     * 公司Id
     */
    private String companyId;
    /**
     * 化学品id
     */
    private String chemicalId;
}
