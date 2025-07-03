package com.abt.market.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * 结算单-其他费用项目
 */
@Table(name = "stlm_expense", indexes = {
        @Index(name = "idx_mid", columnList = "m_id"),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联主表id
     */
    @Column(name="m_id")
    private String mid;

    /**
     * 其他费用说明
     */
    @NotNull(message = "费用说明不能为空", groups = ValidateGroup.Save.class)
    @Size(max = 200, message = "最多不能超过200字", groups = ValidateGroup.Save.class)
    @Column(name="name_", length = 1000)
    private String name;

    /**
     * 其他费用金额
     */
    @Column(name="amt_", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal amount;

    @Column(name="sort_no", columnDefinition = "TINYINT")
    private int sortNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private SettlementMain main;

}
