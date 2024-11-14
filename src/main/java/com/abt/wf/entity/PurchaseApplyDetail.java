package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 采购申请单-物品详情
 */
@Table(name="wf_pur_dtl")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseApplyDetail {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联物品id
     */
    @Column(name="dtl_id")
    private String detailId;

    @ManyToOne
    @JoinColumn(name = "m_id", referencedColumnName = "id")
    private PurchaseApplyMain main;

    /**
     * 物品名称
     */
    @Column(name="name_")
    private String name;

    /**
     * 物品规格型号
     */
    @Column(name="spec_")
    private String specification;

    /**
     * 单位
     */
    @Column(name="unit_")
    private String unit;

    /**
     * 用途
     */
    @Column(name="usage_", length = 128)
    private String usage;

    /**
     * 申请数量
     */
    @Column(name="quantity_")
    private int quantity;

    /**
     * 数量为-1表示未修改;
     * 0表示删除该物品。
     */
    public static final int NO_MODIFY = -1;

    public static final int DELETE = 0;

    /**
     * 业务主管修改后数量
     */
    @Column(name="mgr_modify")
    private int managerModify = NO_MODIFY;
    /**
     * 业务主管
     */
    @Column(name="mgr_id")
    private String managerId;
    @Column(name="mgr_name")
    private String managerName;
    @Column(name="mgr_updatetime")
    private LocalDateTime managerUpdateTime;

    /**
     * 业务副总修改后数量
     */
    @Column(name="leader_modify")
    private int leaderModify= NO_MODIFY;

    /**
     * 业务副总
     */
    @Column(name="leader_id")
    private String leaderId;
    @Column(name="leader_name")
    private String leaderName;
    @Column(name="leader_updatetime")
    private LocalDateTime leaderUpdateTime;

    /**
     * 综合办公室修改
     */
    @Column(name="final_modify")
    private int finalModify = NO_MODIFY;

    /**
     * 综合办公室审批人id
     */
    @Column(name="final_id")
    private String finalId;
    @Column(name="final_name")
    private String finalName;
    @Column(name="final_updatetime")
    private LocalDateTime finalUpdateTime;


    public String getMainId() {
        if (main != null) {
            return main.getId();
        }
        return null;
    }


    /**
     * 该物品被删除
     */
    public boolean isMaterialDeleted() {
        return isManagerDeleted() || isLeaderDeleted() || isFinalDeleted();
    }

    private boolean isManagerDeleted() {
        return this.managerModify == DELETE;
    }

    private boolean isLeaderDeleted() {
        return this.leaderModify == DELETE;
    }

    private boolean isFinalDeleted() {
        return this.finalModify == DELETE;
    }
}
