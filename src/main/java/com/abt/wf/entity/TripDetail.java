package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class TripDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联mid
     */
    @NotNull
    @Column(name="mid")
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
    @Column(name="lodging_exp", columnDefinition="DECIMAL(10,2)")
    private BigDecimal lodgingExpense;

    /**
     * 出差补助
     */
    @Column(name="allowance", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal allowance;

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
    @JoinColumn(name = "mid", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private TripMain main;

    /**
     * 其他项目对象集合
     */
    @OneToMany(mappedBy = "detail", fetch = FetchType.EAGER)
    private List<TripOtherItem> items = new ArrayList<>();

}
