package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 采购申请单-主体
 */
@Getter
@Setter
@Table(name = "wf_pur_main")
@Entity
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseApplyMain extends WorkflowBase{

    /**
     * id
     */
    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 采购申请表单
     */
    @OneToMany(mappedBy = "main", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseApplyDetail> details = new ArrayList<>();

    //-- 审批
    @Transient
    private String decision;
    @Transient
    private String comment;
    @Transient
    private HashMap<String, Object> variableMap = new HashMap<>();

    /**
     * 部门主管
     */
    @Column(name="mgr_userid")
    private String managerUserid;
    @Column(name="mgr_check_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime managerCheckDate;

    /**
     * 主管副总
     */
    @Column(name="leader_userid")
    private String leaderUserid;
    @Column(name="leader_check_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaderCheckDate;

    @Column(name="purchaser_id")
    private String purchaser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="purchaser_check_date")
    private LocalDateTime purchaserCheckDate;

    @Column(name="ceo_id")
    private String ceo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="ceo_check_date")
    private LocalDateTime ceoCheckDate;

    @Transient
    private String saveType;

    /**
     * 是否全部验收
     */
    @Column(name = "is_accepted")
    private boolean isAccepted = false;

    /**
     * 采购总金额
     */
    @Column(name="cost_", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal cost = BigDecimal.ZERO;

    /**
     * 最近验收日期。如果有多次验收，则记录最新的
     */
    @Column(name="accept_date")
    private LocalDateTime acceptDate;

    /**
     * 业务主管审批人
     */
    public static final String KEY_MANAGER = "manager";

    /**
     * 业务副总审批人
     */
    public static final String KEY_LEADER = "leader";

    /**
     * 流程启动人/申请人
     */
    public static final String KEY_STARTER = "starter";

    /**
     * 采购总金额
     */
    public static final String KEY_COST = "cost";

    public static final String KEY_BIZ_ID = "bizId";

    public Map<String, Object> getVariableMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_STARTER, this.getSubmitUserid());
        this.variableMap.put(KEY_MANAGER, this.getManagerUserid());
        this.variableMap.put(KEY_LEADER, this.getLeaderUserid());
        this.variableMap.put(KEY_COST, this.getCost());
        return this.variableMap;
    }

    public void qualified(String acceptItems) {
        this.setAccepted(true);
        this.setAcceptDate(LocalDateTime.now());
        if (this.details != null) {
            this.details.forEach(i -> i.doAccept(acceptItems));
        }
    }

    public void addDetail(PurchaseApplyDetail detail) {
        if (detail == null) {
            return;
        }
        List<PurchaseApplyDetail> list = this.getDetails();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(detail);
        this.setDetails(list);
    }

}
