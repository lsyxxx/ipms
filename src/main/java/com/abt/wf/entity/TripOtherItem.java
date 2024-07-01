package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * 差旅报销-其他项目
 */
@Table(name = "wf_trip_item", indexes = {
        @Index(name = "idx_did", columnList = "did"),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripOtherItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    /**
     * 排序
     */
    @Column(name="sort_", columnDefinition = "TINYINT")
    private int sort;

    /**
     * 说明
     */
    @Column(name="desc_", columnDefinition = "VARCHAR(1000)")
    private String desc;

    /**
     * 项目花费
     */
    @Column(name="expense_", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal expense;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "did")
    private TripDetail detail;

    public TripOtherItem relate(TripDetail detail) {
        this.setDetail(detail);
        return this;
    }


}
