package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 差旅报销
 * 多个明细组成一个报销单。
 * 主数据(保存公共数据)+明细数据(明细数据，不包含公共)
 * 主数据：流程各状态及公共数据的修改只在主数据中修改
 * 明细数据：只保存rootId，以及差旅明细数据，以及processDefKey/instId/DefId这类不可修改的
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "wf_trip")
@Entity
public class TripReimburse extends WorkflowBase {

    @Id
//    @GeneratedValue(generator = "timestampIdGenerator")
//    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="code_", columnDefinition="VARCHAR(128)")
    private String code;

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

    /**
     * 出差事由
     * common
     */
    @Column(columnDefinition = "VARCHAR(1000)")
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_start_date")
    private LocalDate tripStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_end_date")
    private LocalDate tripEndDate;

    @Column(name = "origin_", columnDefinition = "VARCHAR(128)")
    private String tripOrigin;

    @Column(name = "arrival_", columnDefinition = "VARCHAR(128)")
    private String tripArrival;

    /**
     * 补贴天数
     */
    @Column(name = "allowance_dur")
    private Double allowanceDuration;
    /**
     * 补贴金额
     */
    @Column(name = "allowance_exp", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal allowanceExpense =  BigDecimal.ZERO;

    /**
     * 其他费用项目说明
     */
    @Column(name = "oth_exp_desc", columnDefinition = "VARCHAR(512)")
    private String otherExpenseDesc;

    /**
     * 其他费用
     */
    @Column(name = "oth_exp", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal otherExpense =  BigDecimal.ZERO;

    //common
    @Column(name = "sum_", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal sum  =  BigDecimal.ZERO;

    /**
     * 领款人id
     * common
     */
    @Column(name = "payee_id", columnDefinition = "VARCHAR(128)")
    private String payeeId;
    @Column(name="payee_name", columnDefinition="")
    private String payeeName;

    /**
     * 明细排序
     * 根节点是0,其他明细从1开始
     */
    @Column(name = "sort_", columnDefinition = "TINYINT")
    private int sort = 0;

    /**
     * 交通工具, Constants.TRANSPORTATION_*
     */
    @Column(name="transportation", columnDefinition = "VARCHAR(64)")
    private String transportation;

    /**
     * 交通费
     */
    @Column(name = "trans_exp", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal transExpense =  BigDecimal.ZERO;

    //common
    @Column(name = "company_", columnDefinition = "VARCHAR(256)")
    private String company;

    @Column(columnDefinition = "VARCHAR(1600)")
    private String managers;

    /**
     * 附件信息，json格式保存
     */
    @Column(name = "pdf_file", columnDefinition = "VARCHAR(MAX)")
    private String pdfFileList;

    @Column(name = "other_file", columnDefinition = "VARCHAR(MAX)")
    private String otherFileList;


    public TripReimburse copyProcessData(TripReimburse trip) {
        this.setProcessDefinitionKey(trip.getProcessDefinitionKey());
        this.setProcessInstanceId(trip.getProcessInstanceId());
        this.setProcessDefinitionId(trip.getProcessDefinitionId());
        return this;
    }

    /**
     * 计算当前明细的金额合计
     */
    public BigDecimal sumItem() {
        if (this.transExpense == null) {
            this.transExpense = BigDecimal.ZERO;
        }
        if (this.allowanceExpense == null) {
            this.allowanceExpense = BigDecimal.ZERO;
        }
        if (this.otherExpense == null) {
            this.otherExpense = BigDecimal.ZERO;
        }
        this.sum = this.transExpense;
        this.sum = this.sum.add(this.allowanceExpense);
        this.sum = this.sum.add(this.otherExpense);
        return this.sum;
    }
}
