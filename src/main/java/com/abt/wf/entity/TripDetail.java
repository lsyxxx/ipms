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
import java.util.ArrayList;
import java.util.List;

/**
 * 差旅报销详情
 */
@Table(name = "wf_trip_detail", indexes = {
        @Index(name = "idx_mid", columnList = "mid"),
})
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

    /**
     * 其他项目，允许有多个项目
     */
    @Column(name="oth_exp_desc", columnDefinition="VARCHAR(128)")
    private String otherDesc;

    /**
     * 其他费用金额
     */
    @Column(name="oth_exp", columnDefinition="VARCHAR(128)")
    private String otherExpense;
    /**
     * 本次项目合计
     */
    @Column(name="sum_", columnDefinition="DECIMAL(10,2)")
    private BigDecimal sum;
    @Column(name="sort_", columnDefinition = "TINYINT")
    private int sort;

    /**
     * 关联
     * 必须赋值，否则无法保存mid
     */
    @ManyToOne
    @JoinColumn(name = "mid")
    private TripMain main;

    /**
     * 其他项目对象集合
     */
    @OneToMany(mappedBy = "detail", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TripOtherItem> items = new ArrayList<>();


    public TripDetail relate(TripMain main) {
        this.setMain(main);
        return this;
    }


}
