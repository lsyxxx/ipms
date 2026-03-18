package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.finance.entity.Invoice;
import com.abt.wf.model.WithInvoice;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;
import java.util.stream.Collectors;

import static com.abt.wf.config.Constants.*;

@Getter
@Setter
@Entity
@Table(name = "wf_inv_offset")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceOffset extends WorkflowBase implements WithInvoice{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    @Column(name = "company_", nullable = false, columnDefinition = "VARCHAR(64)")
    private String company;

    @NotNull(message = "标题不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "title_", columnDefinition = "VARCHAR(128)")
    private String title;

    @Column(name = "project_", columnDefinition = "VARCHAR(128)")
    private String project;
    @Column(name = "project_id", columnDefinition = "VARCHAR(128)")
    private String projectId;

    /**
     * 项目类型
     */
    @Column(name = "project_type", columnDefinition = "VARCHAR(128)")
    private String projectType;
    /**
     * 累计回票金额
     */
    @Column(name = "acc_inv", columnDefinition = "DECIMAL(10,2)")
    private Double accumulatedInvoice;
    /**
     * 供应商
     */
    @Column(name = "supplier_id", columnDefinition = "VARCHAR(128)")
    private String supplierId;
    /**
     * 供应商名称
     */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "供应商名称不能为空")
    @Column(name = "supplier_name", columnDefinition = "VARCHAR(128)", nullable = false)
    private String supplierName;
    /**
     * 合同名称
     */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "合同名称不能为空")
    @Column(name = "contract_name", columnDefinition = "VARCHAR(128)")
    private String contractName;
    /**
     * 合同编码
     */
    @Column(name = "contract_code", columnDefinition = "VARCHAR(128)")
    private String contractCode;

    /**
     * 合同金额
     */
    @Min(value = 0, groups = {ValidateGroup.Save.class}, message = "合同金额必填，且合同金额必须大于0")
    @Column(name = "contract_amt", columnDefinition = "DECIMAL(10,2)")
    private double contractAmount;
    /**
     * 发票金额
     */
    @Min(value = 0, groups = {ValidateGroup.Save.class}, message = "发票金额必填，且发票金额必须大于0")
    @Column(name = "inv_amt", columnDefinition = "DECIMAL(10,2)")
    private double invoiceAmount;

    /**
     * 发票编号
     */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "发票编号必填")
    @Column(name="inv_code", columnDefinition="VARCHAR(128)")
    private String invoiceCode;

    /**
     * 回票类型
     */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "回票类型必填")
    @Column(name="inv_type", columnDefinition="VARCHAR(128)")
    private String invoiceType;

    /**
     * 备注
     */
    @Column(name="remark_", columnDefinition="VARCHAR(1000)")
    private String remark;

    /**
     * 附件
     */
    @Column(name="file_list", columnDefinition = "TEXT")
    private String fileList;

    /**
     * 审批人
     */
    @Column(name="managers", columnDefinition = "VARCHAR(MAX)")
    private String managers;

    //-------------------------------------
    //  Transient
    //------------------------------------

    @Transient
    private String decision;
    @Transient
    private String comment;
    /**
     * 关联发票列表
     */
    @Transient
    private List<Invoice> invoiceList;

    /**
     * 根据发票列表(invoiceList)生成发票号码字符串
     * 如果发票列表中有数据，那么重新生成
     * 如果发票列表中没数据，那么不变
     */
    public void generateInvoiceCode() {
        if (this.invoiceList != null && this.invoiceList.size() > 0) {
            this.invoiceCode = this.invoiceList.stream().map(Invoice::getCode).collect(Collectors.joining(","));
        }
    }

    @Transient
    private Map<String, Object> variableMap = new HashMap<>();

    public Map<String, Object> createVariableMap() {
        this.variableMap = new HashMap<>();
        variableMap.put(KEY_STARTER, this.getSubmitUserid());
        if (StringUtils.isBlank(this.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.getManagers().split(",")));
        }
        variableMap.put(KEY_SERVICE, SERVICE_INV_OFFSET);
        return this.variableMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InvoiceOffset that = (InvoiceOffset) o;
        return Double.compare(contractAmount, that.contractAmount) == 0 && Double.compare(invoiceAmount, that.invoiceAmount) == 0 && Objects.equals(id, that.id) && Objects.equals(company, that.company) && Objects.equals(project, that.project) && Objects.equals(projectId, that.projectId) && Objects.equals(projectType, that.projectType) && Objects.equals(accumulatedInvoice, that.accumulatedInvoice) && Objects.equals(supplierId, that.supplierId) && Objects.equals(supplierName, that.supplierName) && Objects.equals(contractName, that.contractName) && Objects.equals(contractCode, that.contractCode) && Objects.equals(invoiceCode, that.invoiceCode) && Objects.equals(invoiceType, that.invoiceType) && Objects.equals(remark, that.remark) && Objects.equals(fileList, that.fileList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, company, project, projectId, projectType, accumulatedInvoice, supplierId, supplierName, contractName, contractCode, contractAmount, invoiceAmount, invoiceCode, invoiceType, remark, fileList);
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