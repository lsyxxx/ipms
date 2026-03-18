package com.abt.market.entity;

import com.abt.market.model.SettlementRelationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 结算关联表，仅关联成功绑定的。关联多种表单
 * 比如：结算单关联开票，表中只保存开票成功的(绑定成功)，其他状态则不保存在此表
 */
@Table(name = "stlm_rel", indexes = {
        @Index(name = "idx_mid", columnList = "m_id"),
        @Index(name = "idx_rid", columnList = "r_id")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettlementRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 结算单id
     */
    @Column(name="m_id")
    private String mid;

    /**
     * 关联单据id
     */
    @Column(name="r_id")
    private String rid;

    /**
     * 业务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name="biz_type", length = 16)
    private SettlementRelationType bizType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private SettlementMain main;
}
