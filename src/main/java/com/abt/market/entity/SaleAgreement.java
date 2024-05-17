package com.abt.market.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.abt.market.Constant.DEFAULT_TAX;

@Getter
@Setter
@Entity
@Table(name = "agr_sale")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleAgreement extends AuditInfo {

    @Id
    @Column(name = "id_", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 排序
     */
    @Column(name = "sort_no", columnDefinition = "TINYINT")
    private int sortNo = 0;

    /**
     * 合同类型: 检测服务等
     * 参考enumlib
     */
    @Column(name = "type_", columnDefinition = "VARCHAR(32)")
    private String type;

    @NotNull(message = "合同名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "name_", nullable = false, columnDefinition = "VARCHAR(128)")
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
     * 签订日期
     */
    @Column(name = "sign_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate signDate;

    /**
     * 生效日期年
     */
    @Column(name = "start_year")
    private Integer startYear;

    /**
     * 生效日期月
     */
    @Column(name = "start_mon")
    @Min(value = 1, message = "月份最小为1")
    @Max(value = 12, message = "月份最大为12")
    private Integer startMonth;

    /**
     * 生效日期天(可能没有)
     */
    @Column(name = "start_day")
    @Min(value = 1, message = "合同生效日期-天最小为1")
    @Max(value = 31, message = "合同生效日期-天最大为31")
    private Integer startDay;

    /**
     * 合同到期年
     */
    @Column(name = "end_year")
    private Integer endYear;

    /**
     * 合同到期月
     */
    @Column(name = "end_mon")
    @Min(value = 1, message = "月份最小为1")
    @Max(value = 12, message = "月份最大为12")
    private Integer endMonth;

    /**
     * 合同到期天
     */
    @Column(name = "end_day")
    @Min(value = 1, message = "合同生效日期-天最小为1")
    @Max(value = 31, message = "合同生效日期-天最大为31")
    private Integer endDay;

    /**
     * 合同到期时间
     * 合同签订之后endDuration天到期
     */
    @Column(name = "end_dur")
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
    @Column(name = "sign_name", columnDefinition = "VARCHAR(128)")
    private String signName;
    /**
     * 状态
     */
    @Column(name = "state_", columnDefinition = "VARCHAR(128)")
    private String state;
    /**
     * 开口合同/闭口合同
     */
    @Column(name = "attr_", columnDefinition = "VARCHAR(128)")
    private String attribute;
    /**
     * 是否含税
     */
    @Column(name = "is_tax", columnDefinition = "VARCHAR(128)")
    private boolean isTax;
    /**
     * 税率，填写百分比数字，如6%，就填写6
     */
    @Column(name = "tax_rate", columnDefinition = "DECIMAL(10,2)")
    private double taxRate = DEFAULT_TAX;


}