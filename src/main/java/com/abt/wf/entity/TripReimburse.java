package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 差旅报销
 * 多个明细组成一个报销单。
 * 主数据(保存公共数据)+明细数据(明细数据，不包含公共)
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "wf_trip")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TripReimburse extends WorkflowBase {

    @Id
    @GeneratedValue(generator = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 多个报销明细组成一个报销单，同一个单据中root_id相同
     * 通用数据没有root_id
     */
    @Column(name = "root_id", columnDefinition = "VARCHAR(128)")
    private String rootId;

    //-- common
    @Column(name = "dept_id", columnDefinition = "VARCHAR(128)")
    private String deptId;
    @Column(name = "dept_name", columnDefinition = "VARCHAR(128)")
    private String deptName;

    /**
     * 出差人员
     * common
     */
    @Column(name = "staff_", columnDefinition = "VARCHAR(512)")
    private String staff;

    //common
    @Column(columnDefinition = "VARCHAR(1000)")
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tripStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tripEndDate;

    @Column(name = "origin_", columnDefinition = "VARCHAR(128)")
    private String tripOrigin;

    @Column(name = "arrival_", columnDefinition = "VARCHAR(128)")
    private String tripArrival;

    /**
     * 补贴天数
     */
    @Positive(message = "出差补贴天数必须大于0")
    @Column(name = "allowance_dur")
    private double allowanceDuration;
    /**
     * 补贴金额
     */
    @Positive(message = "出差补贴金额必须大于0")
    @Column(name = "allowance_exp", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal allowanceExpense;

    /**
     * 其他费用项目说明
     */
    @Column(name = "oth_exp_desc", columnDefinition = "VARCHAR(512)")
    private String otherExpenseDesc;

    /**
     * 其他费用
     */
    @Column(name = "oth_exp", columnDefinition = "DECIMAL(10, 2)")
    private String otherExpense;

    //common
    @Column(name = "sum_", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal sum;

    /**
     * 领款人id
     * common
     */
    @Column(name = "payee_id", columnDefinition = "VARCHAR(128)")
    private String payeeId;
    private String payeeName;

    /**
     * 明细排序
     */
    @Column(name = "sort_", columnDefinition = "TINYINT")
    private int sort = 0;

    /**
     * 交通工具, Constants.TRANSPORTATION_*
     */
    @Column(columnDefinition = "VARCHAR(64)")
    private String transportation;

    /**
     * 交通费
     */
    @Column(name = "trans_exp", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal transExpense;

    //common
    @Column(name = "company", columnDefinition = "VARCHAR(256)")
    private String company;
}
