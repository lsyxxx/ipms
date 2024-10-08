package com.abt.market.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import static com.abt.market.Constant.DEFAULT_TAX;

@Getter
@Setter
@Entity
@ToString
@Table(name = "agr_sale")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({CommonJpaAuditListener.class})
public class SaleAgreement extends AuditInfo implements CommonJpaAudit {

    @Id
    @Column(name = "id_", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 排序
     */
    @Column(name = "sort_no")
    private long sortNo = 0;

    /**
     * 合同类型: 检测服务等
     * 参考enumlib
     */
    @Column(name = "type_", columnDefinition = "VARCHAR(32)")
    private String type;

    @NotNull(message = "合同名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "name_", nullable = false, columnDefinition = "VARCHAR(500)")
    private String name;

    /**
     * 甲方名称
     */
    @NotNull(message = "甲方名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "party_a", nullable = false, columnDefinition = "VARCHAR(128)")
    private String partyA;
    @NotNull(message = "乙方名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "party_b", columnDefinition = "VARCHAR(128)")
    private String partyB;
    /**
     * 合同金额，开口合同可能没有，或者非数字类型
     */
    @Column(name = "amount_", columnDefinition = "VARCHAR(128)")
    private String amount;
    /**
     * 合同编号
     */
    @Column(name = "code_", columnDefinition = "VARCHAR(128)")
    private String code;

    /**
     * 签订日期-年
     */
    @Column(name = "sign_year", columnDefinition = "INT")
    private Integer signYear;

    /**
     * 签订日期-月
     */
    @Column(name = "sign_mon", columnDefinition = "TINYINT")
    private Integer signMonth;

    /**
     * 签订日期-日
     */
    @Column(name = "sign_day", columnDefinition = "TINYINT")
    private Integer signDay;

    /**
     * 生效日期年
     */
    @Column(name = "start_year", columnDefinition = "INT")
    private Integer startYear;

    /**
     * 生效日期月
     */
    @Column(name = "start_mon", columnDefinition = "TINYINT")
    @Min(value = 1, message = "月份最小为1")
    @Max(value = 12, message = "月份最大为12")
    private Integer startMonth;

    /**
     * 生效日期天(可能没有)
     */
    @Column(name = "start_day", columnDefinition = "TINYINT")
    @Min(value = 1, message = "合同生效日期-天最小为1")
    @Max(value = 31, message = "合同生效日期-天最大为31")
    private Integer startDay;

    /**
     * 合同到期年
     */
    @Column(name = "end_year", columnDefinition = "INT")
    private Integer endYear;

    /**
     * 合同到期月
     */
    @Column(name = "end_mon", columnDefinition = "TINYINT")
    @Min(value = 1, message = "月份最小为1")
    @Max(value = 12, message = "月份最大为12")
    private Integer endMonth;

    /**
     * 合同到期天
     */
    @Column(name = "end_day", columnDefinition = "TINYINT")
    @Min(value = 1, message = "合同生效日期-天最小为1")
    @Max(value = 31, message = "合同生效日期-天最大为31")
    private Integer endDay;

    /**
     * 合同到期时间
     * 合同签订之后endDuration天到期
     */
    @Column(name = "end_dur", columnDefinition = "INT")
    private Integer endDuration;


    @Column(name = "note_", columnDefinition = "VARCHAR(500)")
    private String note;
    /**
     * 甲方联系人
     */
    @Column(name = "contact_name", columnDefinition = "VARCHAR(32)")
    private String contactName;
    /**
     * 甲方联系方式
     */
    @Column(name = "contact_tel", columnDefinition = "VARCHAR(128)")
    private String contactTel;
    /**
     * 签订人名称
     */
    @Column(name = "sign_name", columnDefinition = "VARCHAR(32)")
    private String signName;
    /**
     * 状态
     */
    @Column(name = "state_", columnDefinition = "VARCHAR(128)")
    private String state;
    /**
     * 开口合同/闭口合同
     */
    @Column(name = "attr_", columnDefinition = "VARCHAR(32)")
    private String attribute;
    /**
     * 是否含税
     */
    @Column(name = "is_tax", columnDefinition = "BIT")
    private boolean includeTax;
    /**
     * 税率，填写百分比数字，如6%，就填写6。
     * 含税由税率
     */
    @Column(name = "tax_rate", columnDefinition = "DECIMAL(10,2)")
    private Double taxRate;

    /**
     * 附件
     */
    @Column(name = "file_list", columnDefinition = "VARCHAR(MAX)")
    private String fileList;

    /**
     * 签订日期
     */
    @Transient
    private String signDateStr;
    /**
     * 合同生效日期
     */
    @Transient
    private String startDateStr;
    /**
     * 合同到期日期
     */
    @Transient
    private String endDateStr;

    /**
     * 根据年月日生成
     */
    public void createSignDateStr() {
        if (this.signYear == null || this.signMonth == null) {
            return;
        }
        String formattedMonth = String.format("%02d", this.signMonth);
        this.signDateStr = this.signYear + "-" + formattedMonth;
        if (this.signDay != null) {
            this.signDateStr = this.signDateStr + "-" + String.format("%02d", this.signDay);
        }
    }

    public void createStartDateStr() {
        if (this.startYear == null || this.startMonth == null) {
            return;
        }
        String formattedMonth = String.format("%02d", this.startMonth);
        this.startDateStr = this.startYear + "-" + formattedMonth;
        if (this.startDay != null) {
            this.startDateStr = this.startDateStr + "-" + String.format("%02d", this.startDay);
        }
    }

    public void createEndDateStr() {
        if (this.endDuration != null) {
            this.endDateStr = "签订之日后" + this.endDuration + "天";
        } else if (this.endYear != null && this.endMonth != null) {
            this.endDateStr = this.endYear + "-" + String.format("%02d", this.endMonth);
            if (this.endDay != null) {
                this.endDateStr = this.endDateStr + "-" + String.format("%02d", this.endDay);
            }
        }
    }

    public void format() {
        this.createStartDateStr();
        this.createSignDateStr();
        this.createEndDateStr();
    }


    public void defaultTaxRate() {
        if (includeTax) {
            this.taxRate = DEFAULT_TAX;
        }
    }


}