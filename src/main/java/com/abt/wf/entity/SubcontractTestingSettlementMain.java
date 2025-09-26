package com.abt.wf.entity;

import com.abt.sys.model.entity.SystemFile;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.model.TaskCheckUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;

/**
 * 外送检测主表
 */
@Table(name = "wf_sbct_stl_main")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubcontractTestingSettlementMain extends WorkflowBase{

    @Id
    @GeneratedValue(generator  = "subcontractTestIdGenerator")
    @GenericGenerator(
            name = "subcontractTestIdGenerator", type = com.abt.wf.generator.CommonIdGenerator.class,
            parameters = {@Parameter(name = "code", value = "FBJS")}
    )
    private String id;

    /**
     * 外送日期开始
     */
    @Column(name="sbct_start_date")
    private LocalDate sbctStartDate;

    /**
     * 外送日期结束
     */
    @Column(name="sbct_end_date")
    private LocalDate sbctEndDate;

    /**
     * 外送单位
     */
    @Column(name="sbct_company")
    private String subcontractCompany;

    /**
     * 报告返回日期开始
     */
    @Column(name="report_start_date")
    private LocalDate reportStartDate;

    /**
     * 报告返回日期结束
     */
    @Column(name="report_end_date")
    private LocalDate reportEndDate;

    @OneToMany(mappedBy = "main", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<SubcontractTestingSettlementDetail> details;

    /**
     * 合计总金额(不含税)
     */
    @Column(name="cost_no_tax", precision = 10, scale = 2)
    private BigDecimal costNoTax;

    /**
     * 合计金额，含税
     */
    @Column(name="cost_tax", precision = 10, scale = 2)
    private BigDecimal costWithTax;

    /**
     * 税率，保存小数，默认0.01（1%）
     */
    @Column(name="tax_rate", precision = 7, scale = 4)
    private BigDecimal taxRate = BigDecimal.valueOf(0.01);

    /**
     * 获取税率百分比
     */
    private BigDecimal getTaxRatePercentage() {
     return this.taxRate == null ? null : this.taxRate.multiply(BigDecimal.valueOf(100));
    }

    @Column(name="remark_", length = 1000)
    private String remark;

    @Column(name="attachments", columnDefinition = "TEXT")
    private List<SystemFile> attachments;

    //-- 审批
    @Transient
    private String decision;
    @Transient
    private String comment;
    @Transient
    private HashMap<String, Object> variableMap = new HashMap<>();

    /**
     * 审批人
     */
    @Transient
    private List<TaskCheckUser> checkUsers;

    @Transient
    private List<SbctSummaryData> summaryData;

    /**
     * 是否存在重复结算的情况
     */
    @Transient
    private boolean duplicatedSettledExists = false;

    public Map<String, Object> createVarMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_STARTER, this.getSubmitUserid());
        this.variableMap.put(KEY_SERVICE, SERVICE_SBCT_STL);
        return this.variableMap;
    }


    /**
     * 删除一条记录
     */
    private void removeDetail(String id) {
        SubcontractTestingSettlementDetail removed = this.details.stream()
                .filter(dtl -> dtl.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (removed != null) {
            details.remove(removed);
            removed.setMain(null);
        }
    }
    
    /**
     * 获取抄送人列表
     * @return 抄送人ID列表
     */
    public List<String> copyList() {
        // 这里可以根据业务需求实现抄送人列表逻辑
        return new ArrayList<>();
    }

}
