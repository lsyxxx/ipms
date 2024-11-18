package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.KEY_MANAGER;

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
public class PurchaseApplyMain extends WorkflowBase {

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
    @OneToMany(mappedBy = "main", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    private List<PurchaseApplyDetail> details = new ArrayList<>();

    //-- 审批
    @Transient
    private String decision;
    @Transient
    private String comment;
    @Transient
    private HashMap<String, Object> variableMap = new HashMap<>();

    @Transient
    private String managerUserid;

    @Transient
    private String leaderUserid;


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

    public Map<String, Object> getVariableMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_STARTER, this.getSubmitUserid());
        this.variableMap.put(KEY_MANAGER, this.getManagerUserid());
        this.variableMap.put(KEY_LEADER, this.getLeaderUserid());
        return this.variableMap;
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
