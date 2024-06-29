package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * 差旅报销-其他项目
 */
@Table(name = "wf_trip_other", indexes = {
        @Index(name = "idx_did", columnList = "did")
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
    private int sort;

    /**
     * 说明
     */
    private String desc;

    /**
     * 项目花费
     */
    private BigDecimal expense;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "did")
    private TripDetail detail;

    public TripOtherItem relate(TripDetail detail) {
        this.setDetail(detail);
        return this;
    }


}
