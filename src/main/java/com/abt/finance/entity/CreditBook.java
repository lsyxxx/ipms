package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.service.InsertJpaUser;
import com.abt.common.service.UserJpaAudit;
import com.abt.wf.entity.FlowOperationLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 贷方记账，资金流出
 * 审核信息仍然保存在wf_opt_log中
 */
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "fi_credit_book")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({InsertJpaUser.class})
public class CreditBook extends AuditInfo implements UserJpaAudit {
    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    @NotNull
    @Column(name="company_", columnDefinition = "VARCHAR(256)")
    private String company;

    /**
     * 记账对应服务名称，如费用报销
     */
    @Column(name="srv_name", columnDefinition="VARCHAR(128)")
    private String serviceName;


    /**
     * 业务实体id
     */
    @Column(name="biz_id", columnDefinition="VARCHAR(128)")
    private String businessId;

    /**
     * 金额
     */

    @NotNull
    @Column(name="amt", columnDefinition="DECIMAL(10,2)")
    private Double amount;

    /**
     * 事由
     */
    @Column(name="reason_", columnDefinition="VARCHAR(1000)")
    private String reason;


    /**
     * 关联项目id
     */
    @Column(name="proj_id", columnDefinition="VARCHAR(128)")
    private String projectId;

    /**
     * 关联项目名称
     */
    @Column(name="proj_name", columnDefinition = "VARCHAR(1000)")
    private String projectName;


    /**
     * 票据种类
     * 白条，收据，发票
     */
    @Column(name="inst_type")
    private String instrumentType;

    /**
     * 票据数量
     */
    @Column(name="inst_num", columnDefinition="TINYINT")
    private Integer instrumentNum;

    @Column(name="file_json", columnDefinition = "VARCHAR(MAX)")
    private String fileJson;

    /**
     * 收款用户name
     */
    @Column(name="rec_username", columnDefinition = "VARCHAR(32)")
    private String receiveUsername;


    /**
     * 关联税务会计科目id
     */
    @Column(name="tax_item_id", columnDefinition="VARCHAR(128)")
    private String taxItemId;
    @Column(name="tax_item_name", length = 64)
    private String taxItemName;
    @Column(name="tax_item_code", length = 64 )
    private String taxItemCode;

    /**
     * 关联核算会计科目id
     */
    @Column(name="acc_item_id", columnDefinition="VARCHAR(128)")
    private String accountItemId;
    @Column(name="acc_item_name", length = 64)
    private String accountItemName;
    @Column(name="acc_item_code", length = 64 )
    private String accountItemCode;

    /**
     * 付款方式
     */
    @NotNull
    @Column(name="pay_type", columnDefinition="VARCHAR(128)")
    private String payType;
    /**
     * 付款时间
     */
    @Column(name="pay_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payDate;

    /**
     * 付款账号
     */
    @Column(name="pay_acc", columnDefinition="VARCHAR(128)")
    private String payAccount;

    /**
     * 付款账号银行
     */
    @Column(name="pay_bank", columnDefinition="VARCHAR(128)")
    private String payBank;

    /**
     * 付款执行时间
     */
    @Column(name="real_pay_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate realPayDate;


    /**
     * 关联审批
     */
//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
//    private List<FlowOperationLog> logs;


    /**
     * 申请人部门id
     */
    @Column(name="dept_id", columnDefinition = "VARCHAR(128)")
    private String departmentId;

    /**
     * 申请人部门name
     */
    @Column(name="dept_name", columnDefinition = "VARCHAR(128)")
    private String departmentName;

    /**
     * 班组id
     */
    @Column(name="dept_team_id", columnDefinition = "VARCHAR(128)")
    private String teamId;

    /**
     * 班组name
     */
    @Column(name="dept_team_name", columnDefinition = "VARCHAR(128)")
    private String teamName;


}

