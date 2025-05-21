package com.abt.wf.entity;

import com.abt.common.listener.JpaListStringConverter;
import com.abt.common.listener.JpaListUserConverter;
import com.abt.common.model.User;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import com.abt.wf.model.TaskCheckUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.entity.PurchaseApplyMain.KEY_LEADER;

/**
 * 外送检测
 */
@Table(name = "wf_sbct")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubcontractTesting extends WorkflowBase {
    @Id
    @GeneratedValue(generator  = "subcontractTestIdGenerator")
    @GenericGenerator(name = "subcontractTestIdGenerator", type = com.abt.wf.generator.SubcontractTestIdGenerator.class)
    private String id;

    @Column(name = "sample_type", length = 255)
    private String sampleType; // 样品种类

    /**
     * 样品数量
     */
    @Column(name="sample_num")
    private int sampleNum;

    /**
     * 上传样品清单
     */
    @Convert(converter = SystemFileListConverter.class)
    @Column(name = "upload_sample_list", columnDefinition = "VARCHAR(MAX)")
    private List<SystemFile> uploadSampleList; // 上传样品清单

    @Size(max = 1000, message = "外送原因最多不超过250字")
    @Column(name="reason_", length = 1000)
    private String reason;

    /**
     * 委托单位名称
     */
    @Column(name="subcontract_company_name")
    private String subcontractCompanyName;
    /**
     * 委托单位id，关联t_subcontract_info
     */
    @Column(name="subcontract_company_id")
    private String subcontractCompanyId;

    /**
     * 纳税人识别号
     */
    @Column(name="tax_no")
    private String taxNo;

    /**
     * 是否评价为分包商
     */
    @Column(name="is_subcontract_company", columnDefinition = "BIT")
    private Boolean isSubcontractCompany;


    @Column(name = "is_cma", columnDefinition = "BIT")
    private Boolean isCMA; // 是否需要CMA

    @Column(name = "is_contract", columnDefinition = "BIT")
    private Boolean isContract; // 是否签合同

    /**
     * 是否开口合同
     */
    @Column(name="is_open_contract", columnDefinition = "BIT")
    private Boolean isOpenContract;

    /**
     * 合同文件
     */
    @Convert(converter = SystemFileListConverter.class)
    @Column(name="contract_file", columnDefinition = "VARCHAR(MAX)")
    private List<SystemFile> contractFile;

    /**
     * 是否开发票
     */
    @Column(name="is_invoice", columnDefinition = "BIT")
    private Boolean isInvoice;

    /**
     * 是否使用现金支付
     */
    @Column(name="is_cash", columnDefinition = "BIT")
    private Boolean isCash;

    @Column(name = "cost_")
    private Double cost; // 费用总金额

    /**
     * 计划送样日期
     */
    @Column(name="plan_send_sample_date")
    private LocalDate planSendSampleDate;

    /**
     * 计划验收日期
     */
    @Column(name="plan_accept_date")
    private LocalDate planAcceptDate;

    /**
     * 计划交付日期
     */
    @Column(name = "plan_completion_date", length = 255)
    private LocalDate planCompletionDate;


    /**
     * 报告交付内容
     * 电子文件/纸质，可多选
     */
    @Convert(converter = JpaListStringConverter.class)
    @Column(name = "report_delivery", length = 255)
    private List<String> reportDelivery;

    @Column(name = "remarks", length = 255)
    private String remarks; // 备注

    /**
     * 抄送人
     */
    @Convert(converter = JpaListUserConverter.class)
    @Column(name="notify_users", columnDefinition = "VARCHAR(MAX)")
    private List<User> notifyUsers;

    /**
     * 业务归属, abt/grd/dc
     */
    @Column(name="company_", length = 32)
    private String company;

    @Transient
    private Map<String, Object> variableMap;

    @Transient
    private List<TaskCheckUser> checkUsers;

    @Transient
    private String decision;
    @Transient
    private String comment;


    @OneToMany(mappedBy = "main")
    private List<SubcontractTestingSample> sampleList;

    public static final String KEY_IS_OPEN_CONTRACT = "isOpenContract";

    public Map<String, Object> createVarMap() {
        if (this.variableMap == null) {
            this.variableMap = new HashMap<>();
        }
        this.variableMap.put(KEY_STARTER, this.getSubmitUserid());
        this.variableMap.put(KEY_STARTER_NAME, this.getSubmitUsername());
        this.variableMap.put(KEY_COST, this.getCost());
        this.variableMap.put(KEY_SERVICE, SERVICE_SUBCONTRACT_TESTING);
        this.variableMap.put(KEY_IS_OPEN_CONTRACT, this.isOpenContract);
        this.variableMap.put(KEY_NOTIFY_USERS, this.notifyUsers);
        return this.variableMap;
    }
}
