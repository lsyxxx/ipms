package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.Constants.KEY_MANAGER;

/**
 * 开票申请业务
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "wf_inv")
@Entity
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
public class InvoiceApply extends WorkflowBase {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 发票类型: 专票，普票
     */
    @Column(name="inv_type", columnDefinition="VARCHAR(32)")
    private String invoiceType;
    /**
     * 项目
     */
    @Column(name="project", columnDefinition="VARCHAR(128)")
    private String project;
    /**
     * 开票金额
     */
    @Column(name="inv_amt", columnDefinition="DECIMAL(10,2)")
    private double invoiceAmount;

    //-------------------
    //--- 开票信息
    //-------------------
    /**
     * 客户
     */
    @Column(name="client_id", columnDefinition="VARCHAR(128)")
    private String clientId;
    @Column(name="client_name", columnDefinition="VARCHAR(128)")
    private String clientName;

    /**
     * 地址
     */
    @Column(name="address", columnDefinition="VARCHAR(128)")
    private String address;
    /**
     * 税号
     */
    @Column(name="tax_no", columnDefinition="VARCHAR(128)")
    private String taxNo;
    /**
     * 电话
     */
    @Column(name="tel_no", columnDefinition="VARCHAR(32)")
    private String telephoneNo;

    /**
     * 开户行
     */
    @Column(name="bank", columnDefinition="VARCHAR(128)")
    private String bank;

    /**
     * 银行账号
     */
    @Column(name="account", columnDefinition="VARCHAR(32)")
    private String account;

    /**
     * 合同金额
     */
    @Positive(message = "合同金额必须大于0", groups = {ValidateGroup.Apply.class})
    @NotNull(message = "合同金额必填", groups = {ValidateGroup.Apply.class})
    @Column(name="contract_amt", columnDefinition="DECIMAL(10,2)")
    private Double contractAmount;

    /**
     * 合同编号
     */
    @NotNull(message = "合同编号(contractNo)不能为空", groups = {ValidateGroup.Apply.class})
    @Column(name="contract_no", columnDefinition="VARCHAR(64)")
    private String contractNo;
    /**
     * 合同名称
     */
    @Column(name="contract_name", columnDefinition="VARCHAR(128)")
    private String contractName;

    /**
     * 备注
     */
    @NotNull(message = "备注必填", groups = {ValidateGroup.Apply.class})
    @Column(name="remark", columnDefinition="VARCHAR(1000)")
    private String remark;

    /**
     * 选择审批人
     */
    @Column(columnDefinition="VARCHAR(1600)")
    private String managers;

    /**
     * 申请部门
     */
    @Column(name="dept_id", columnDefinition="VARCHAR(128)")
    private String deptId;
    @Column(name="dept_name", columnDefinition="VARCHAR(32)")
    private String deptName;
    /**
     * 班组
     */
    @Column(name="team_id", columnDefinition="VARCHAR(128)")
    private String teamId;
    @Column(name="team_name", columnDefinition="VARCHAR(32)")
    private String teamName;

    /**
     * 业务所属公司
     */
    @NotNull(message = "业务所属公司", groups = {ValidateGroup.Apply.class})
    @Column(name="company_", columnDefinition="VARCHAR(32)")
    private String company;

    /**
     * 附件json
     */
    @Column(name="file_list", columnDefinition="VARCHAR(MAX)")
    private String fileList;


    @Transient
    private String comment;
    @Transient
    private String decision;
    @Transient
    private Map<String, Object> variableMap = new HashMap<String, Object>();

    public Map<String, Object> createVariableMap() {
        this.variableMap = new HashMap<>();
        variableMap.put(KEY_STARTER, this.getSubmitUserid());
        variableMap.put(KEY_COST, this.getInvoiceAmount());
        if (StringUtils.isBlank(this.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.getManagers().split(",")));
        }

        return this.variableMap;
    }

}
