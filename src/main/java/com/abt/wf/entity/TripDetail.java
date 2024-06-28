package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 */
@Table(name = "wf_trip_detail")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDetail extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "mid")
    private String mid;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    /**
     * 天数
     */
    @Column(name="day_sum")
    private int daySum;

    /**
     * 出发地
     */
    @Column(name="origin_", length = 32)
    private String tripOrigin;

    /**
     * 目的地
     */
    @Column(name="arrival_", length = 32)
    private String tripArrival;

    @Column(name="transportation", length = 32)
    private String transportation;

    @Column(name="trans_exp", columnDefinition="DECIMAL(10,2)")
    private BigDecimal transExpense;

    /**
     * 住宿天数
     */
    @Column(name="lodging_day")
    private int lodgingDay;

    /**
     * 住宿费-金额
     */
    @Column(name="lodging_amt", columnDefinition="DECIMAL(10,2)")
    private BigDecimal lodgingAmount;

    @Column(name="oth_exp_desc", columnDefinition="VARCHAR(128)")
    private String otherExpenseDesc;

    @Column(name="oth_exp", columnDefinition="VARCHAR(128)")
    private String otherExpense;
    private BigDecimal sum;
    private int sort;

}
