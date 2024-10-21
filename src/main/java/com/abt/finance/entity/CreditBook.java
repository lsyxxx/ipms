package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.service.InsertJpaUser;
import com.abt.finance.service.ICreditBook;
import com.abt.wf.entity.FlowOperationLog;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.LocalDate;
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
@Table(name = "fi_credit_book", indexes={
    @Index(name = "idx_biz_id", columnList = "biz_id"),})
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({InsertJpaUser.class})
public class CreditBook extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
     * 经办人姓名
     */
    @Column(name="user_name", columnDefinition = "VARCHAR(32)")
    private String username;

    /**
     * 业务实体id
     */
    @NotNull
    @Column(name="biz_id", columnDefinition="VARCHAR(128)")
    private String businessId;

    /**
     * 金额
     */

    @NotNull
    @Column(name="cost", columnDefinition="DECIMAL(10,2)")
    private Double cost;

    /**
     * 事由
     */
    @NotNull
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
     * 收款用户name
     */
    @Column(name="rec_user  ", columnDefinition = "VARCHAR(32)")
    private String receiveUser;


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


    /**
     * 付款级别:正常，加急，特急
     */
    @Column(name="pay_lv", columnDefinition = "VARCHAR(16)")
    private String payLevel;

    @Column(name="payt_type", length = 32)
    private String payType;

    /**
     * 付款账号id
     */
    @Column(name="pay_acc_id", columnDefinition="VARCHAR(128)")
    private String payAccountId;

    @OneToOne
    @JoinColumn(name = "pay_acc_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private BankAccount payBankAccount;

    /**
     * 付款时间
     */
    @Column(name="pay_date")
    private LocalDate payDate;
    /**
     * 关联税务会计科目id
     */
    @Column(name="tax_item_id", columnDefinition="VARCHAR(128)")
    private String taxItemId;

    @OneToOne
    @JoinColumn(name = "tax_item_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private AccountItem taxItem;

    /**
     * 关联核算会计科目id
     */
    @Column(name="acc_item_id", columnDefinition="VARCHAR(128)")
    private String accountItemId;

    @OneToOne
    @JoinColumn(name = "acc_item_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private AccountItem accountItem;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "biz_id", referencedColumnName = "entity_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
//    @NotFound(action= NotFoundAction.IGNORE)
//    private List<FlowOperationLog> flowLogs;

    public static CreditBook create(ICreditBook creditBook) {
        CreditBook cb = new CreditBook();
        cb.setBusinessId(creditBook.getBusinessId());
        cb.setReason(creditBook.getReason());
        cb.setCompany(creditBook.getCompany());
        cb.setAccountItemId(creditBook.getAccountItemId());
        cb.setTaxItemId(creditBook.getTaxItemId());
        cb.setPayDate(creditBook.getPayDate());
        cb.setPayType(creditBook.getPayType());
        cb.setPayAccountId(creditBook.getPayAccountId());



        return cb;
    }

}

