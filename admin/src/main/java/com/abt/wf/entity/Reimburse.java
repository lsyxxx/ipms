package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.finance.entity.AccountItem;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.entity.Invoice;
import com.abt.finance.service.ICreditBook;
import com.abt.wf.model.WithInvoice;
import com.abt.wf.model.WorkflowCompany;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.abt.wf.config.Constants.*;

/**
 *
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Table(name = "wf_rbs")
@DynamicInsert
@DynamicUpdate
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reimburse extends WorkflowBase implements ICreditBook, WithInvoice, WorkflowCompany {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    @NotNull(groups = {ValidateGroup.Preview.class, ValidateGroup.Save.class})
//    @Positive(message = "报销金额不能小于0.00")
    @Column(name="cost", columnDefinition="DECIMAL(10, 2)")
    private Double cost;

    /**
     * 原借款，借了准备金需要填写，没有则不填
     */
    @PositiveOrZero(message = "原借款金额不能小于0.00")
    @Column(name="reserve_loan", columnDefinition="DECIMAL(10, 2)")
    private Double reserveLoan;

    /**
     * 应退余额，借了准备金有退款余额，没有则不填
     */
//    @Positive(message = "应退金额不能小于0.00")
    @Column(name="reserve_refund", columnDefinition="DECIMAL(10, 2)")
    private Double reserveRefund;

    @NotBlank
    @Column(name="reason_", columnDefinition="VARCHAR(1000)")
    private String reason;

    /**
     * 报销日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="rbs_date")
    private LocalDate rbsDate;

    @Max(value = 99, message = "票据数量不能超过99")
    @Min(value = 0, message = "票据数量最小为0")
    @Column(name="voucher_num", columnDefinition="TINYINT")
    private int voucherNum;
    /**
     * 报销类型
     */
    @NotNull
    @Column(name="rbs_type", columnDefinition="VARCHAR(256)")
    private String rbsType;

    /**
     * 公司, abt/grd
     */
    @NotNull
    @Column(name="company", columnDefinition="VARCHAR(256)")
    private String company;
    /**
     * 关联项目
     */
    @Column(columnDefinition="VARCHAR(1000)")
    private String project;

    /**
     * 部门
     */
    @Column(name="dept_id", columnDefinition="VARCHAR(128)")
    private String departmentId;
    @Column(name="dept_name", columnDefinition="VARCHAR(128)")
    private String departmentName;

    /**
     * 班组/科室Id
     */
    @Column(name="team_id", columnDefinition="VARCHAR(128)")
    private String teamId;
    /**
     * 班组/科室名称
     */
    @Column(name = "team_name", columnDefinition="VARCHAR(128)")
    private String teamName;

    /**
     * 附件信息，json格式保存
     */
    @Column(name = "pdf_file", columnDefinition = "TEXT")
    private String pdfFileList;

    @Column(name = "other_file", columnDefinition = "TEXT")
    private String otherFileList;

    @Column(name="rec_user")
    private String receiveUser;

    /**
     * 选择的审批人 json
     */
    @Column(columnDefinition="VARCHAR(1600)")
    private String managers;

    /**
     * 付款级别:正常，加急，特急
     */
    @Column(name="pay_lv", columnDefinition = "VARCHAR(16)")
    private String payLevel;

    @Column(name="pay_type", length = 32)
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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


    //-- 审批
    @Transient
    private String decision;
    @Transient
    private String comment;
    @Transient
    private HashMap<String, Object> variableMap = new HashMap<>();

    @Transient
    private List<Invoice> invoiceList;

    @Transient
    private List<CostDetail> costDetailList;

    //-- 流程参数key
    public static final String KEY_COST = "cost";
    public static final String KEY_IS_LEADER = "isLeader";
    public static final String KEY_STARTER = "starter";
    public static final String KEY_STARTER_NAME = "starterName";

    public HashMap<String, Object> createVariableMap() {
        this.variableMap.clear();
        if (StringUtils.isBlank(this.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.getManagers().split(",")));
        }
        this.variableMap.put(KEY_STARTER, this.getSubmitUserid());
        this.variableMap.put(KEY_STARTER_NAME, this.getSubmitUsername());
        this.variableMap.put(KEY_COST, this.getCost());
        this.variableMap.put(KEY_SERVICE, "费用报销");
        this.variableMap.put(KEY_COMPANY, this.getCompany());
        return this.variableMap;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Reimburse reimburse = (Reimburse) o;
        return voucherNum == reimburse.voucherNum && Objects.equals(id, reimburse.id) && Objects.equals(cost, reimburse.cost) && Objects.equals(reserveLoan, reimburse.reserveLoan) && Objects.equals(reserveRefund, reimburse.reserveRefund) && Objects.equals(reason, reimburse.reason) && Objects.equals(rbsDate, reimburse.rbsDate) && Objects.equals(rbsType, reimburse.rbsType) && Objects.equals(company, reimburse.company) && Objects.equals(project, reimburse.project) && Objects.equals(departmentId, reimburse.departmentId) && Objects.equals(departmentName, reimburse.departmentName) && Objects.equals(teamId, reimburse.teamId) && Objects.equals(teamName, reimburse.teamName) && Objects.equals(pdfFileList, reimburse.pdfFileList) && Objects.equals(otherFileList, reimburse.otherFileList) && Objects.equals(managers, reimburse.managers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, cost, reserveLoan, reserveRefund, reason, rbsDate, voucherNum, rbsType, company, project, departmentId, departmentName, teamId, teamName, pdfFileList, otherFileList, managers);
    }


    @Override
    public String getBusinessId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return getCreateUsername();
    }

    @Override
    public Double getExpense() {
        return this.cost;
    }

    @Override
    public String getFileJson() {
        return this.otherFileList;
    }

    @Override
    public LocalDateTime getBizCreateDate() {
        return getCreateDate();
    }

    @Override
    public void clearData() {
        setPayDate(null);
        setPayBankAccount(null);
        setPayType(null);
        setPayLevel(null);
        setPayAccountId(null);
        setAccountItem(null);
        setAccountItemId(null);
        setTaxItem(null);
        setTaxItemId(null);
    }

    @Override
    public String getRefCode() {
        return this.id;
    }

    @Override
    public String getRefName() {
        return this.getServiceName();
    }
}
