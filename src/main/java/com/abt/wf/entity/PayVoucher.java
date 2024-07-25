package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.wf.config.Constants;
import com.abt.wf.listener.JpaWorkflowListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;

/**
 * 款项支付单
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "wf_pay_voucher")
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
public class PayVoucher extends WorkflowBase{
    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 项目名称
     */
    @Column(name="project", columnDefinition="VARCHAR(128)")
    private String project;

    /**
     * 此次支付发票及单据数量
     */
    @Column(name="pay_inv_num", columnDefinition="TINYINT")
    private int payInvoiceNum;
    /**
     * 付款金额
     */
    @Column(name="pay_amt", columnDefinition="DECIMAL(10,2)")
    @NotNull(message = "付款金额必填", groups = {ValidateGroup.Apply.class, ValidateGroup.Preview.class})
    private BigDecimal payAmount;

    /**
     * 合同名称
     */
    @NotNull(message = "合同名称必填", groups = {ValidateGroup.Apply.class})
    @Column(name="contract_name", columnDefinition="VARCHAR(128)")
    private String contractName;

    /**
     * 合同内容
     */
    @NotNull(message = "合同内容必填", groups = {ValidateGroup.Apply.class})
    @Column(name="contract_desc", columnDefinition="VARCHAR(1000)")
    private String contractDesc;
    /**
     * 合同编号
     */
    @Column(name="contract_no", columnDefinition="VARCHAR(128)")
    private String contractNo;
    /**
     * 合同金额
     */
    @Column(name="contract_amt", columnDefinition="DECIMAL(10,2)")
    private BigDecimal contractAmount;

    /**
     * 已付款金额
     */
    @Column(name="payed_amt", columnDefinition="DECIMAL(10,2)")
    private BigDecimal payedAmount;
    /**
     * 已开具发票
     * TODO: 待确认
     */
    @Column(name="payed_inv_num", columnDefinition="TINYINT")
    private int payedInvoiceNum;

    /**
     * 付款说明
     */
    @Column(name="pay_desc", columnDefinition="VARCHAR(1000)")
    private String payDesc;
    /**
     * 收款人
     */
    @NotNull(groups = {ValidateGroup.Apply.class}, message = "收款人必填")
    @Column(name="rec_user", columnDefinition="VARCHAR(128)")
    private String receiveUser;

    /**
     * 收款人开户行
     */
    @Column(name="rec_bank", columnDefinition="VARCHAR(128)")
    private String receiveBank;

    /**
     * 收款人账户
     */
    @Column(name="rec_account", columnDefinition="VARCHAR(128)")
    private String receiveAccount;

    /**
     * 电子发票附件
     * json
     */
    @Column(name="pdf_file", columnDefinition="TEXT")
    private String pdfFileList;

    /**
     * 其他附件
     * json
     */
    @Column(name="other_file", columnDefinition="TEXT")
    private String otherFileList;

    @Column(name="managers", columnDefinition="VARCHAR(1600)")
    private String managers;

    @Column(name="company_", columnDefinition="VARCHAR(16)")
    private String company;

    @Transient
    private String comment;
    @Transient
    private String decision;
    @Transient
    private Map<String, Object> variableMap = new HashMap<String, Object>();

    public Map<String, Object> createVarMap() {
        this.variableMap = new HashMap<>();
        variableMap.put(KEY_STARTER, this.getSubmitUserid());
        variableMap.put(KEY_COST, this.getPayAmount().doubleValue());
        if (StringUtils.isBlank(this.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.getManagers().split(",")));
        }
        return this.variableMap;
    }


}
