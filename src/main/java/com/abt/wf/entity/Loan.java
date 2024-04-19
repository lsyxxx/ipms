package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.wf.listener.JpaWorkflowListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.Constants.KEY_MANAGER;

/**
 * 借款单
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "wf_loan")
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends WorkflowBase{
    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 单据及附件数量
     */
    @Column(name="vch_num", columnDefinition="TINYINT")
    private int voucherNum;
    /**
     * 借款部门id
     */
    @NotNull(message = "借款部门不能为空", groups = { ValidateGroup.Apply.class })
    @Column(name="dept_id", columnDefinition="VARCHAR(128)")
    private String deptId;
    @Column(name="dept_name", columnDefinition="VARCHAR(64)")
    private String deptName;
    /**
     * 借款金额
     */
    @Positive(message = "借款金额必须为正数", groups = { ValidateGroup.Apply.class })
    @Column(name="loan_amt", columnDefinition="DECIMAL(10,2)")
    private double loanAmount;

    /**
     * 借款事由
     */
    @NotNull(groups = { ValidateGroup.Apply.class }, message = "借款事由不能为空")
    @Column(name="reason_", columnDefinition="VARCHAR(1000)")
    private String reason;
    /**
     * 项目名称
     */
    @Column(name="project", columnDefinition="VARCHAR(128)")
    private String project;

    /**
     * 收款人
     */
    @Column(name="rec_user", columnDefinition="VARCHAR(128)")
    private String receiveUser;

    /**
     * 开户行
     */
    @Column(name="rec_bank", columnDefinition="VARCHAR(128)")
    private String receiveBank;
    /**
     * 账户
     */
    @Column(name="rec_account", columnDefinition="VARCHAR(128)")
    private String receiveAccount;

    /**
     * 支付方式(转账，现金)
     */
    @Column(name="pay_type", columnDefinition="VARCHAR(32)")
    private String payType;

    /**
     * 选择审批人
     */
    @Column(columnDefinition="VARCHAR(1600)")
    private String managers;

    @Column(columnDefinition="VARCHAR(MAX)")
    private String fileList;

    @Column(name = "company_", length = 32)
    private String company;

    @Transient
    private String comment;
    @Transient
    private String decision;

    @JsonIgnore
    @Transient
    private Map<String, Object> variableMap = new HashMap<String, Object>();

    public Map<String, Object> createVarMap() {
        this.variableMap = new HashMap<>();
        variableMap.put(KEY_STARTER, this.getSubmitUserid());
        variableMap.put(KEY_COST, this.getLoanAmount());
        if (StringUtils.isBlank(this.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.getManagers().split(",")));
        }
        return this.variableMap;
    }

}
