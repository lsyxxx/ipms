package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
     * 关联detail id
     */
    @NotNull
    @Column(name="did")
    private String did;

    /**
     * 排序
     */
    @Column(name="sort_", columnDefinition = "TINYINT")
    private int sort;

    /**
     * 说明
     */
    @Column(name="desc_", columnDefinition = "VARCHAR(1000)")
    private String description;

    /**
     * 项目花费
     */
    @Column(name="expense_", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal expense;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "did", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
//    @JsonIgnore
//    @JsonBackReference
//    private TripDetail detail;


}
