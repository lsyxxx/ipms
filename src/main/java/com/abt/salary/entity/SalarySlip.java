package com.abt.salary.entity;

import com.abt.common.model.AuditInfo;
import com.abt.sys.exception.BusinessException;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 */
@Getter
@Setter
@Entity
@Table(name = "sl_slip", indexes = {
        @Index(name = "idx_m_id", columnList = "m_id"),
        @Index(name = "idx_r_id", columnList = "r_id"),

})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalarySlip extends AuditInfo {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联SalaryMain id
     */
    @Column(name="m_id", columnDefinition="VARCHAR(255)", nullable = false)
    private String mainId;

    /**
     * 关联row_id
     */
    @Column(name="r_id", columnDefinition="VARCHAR(255)", nullable = false)
    private String rowId;

    @Column(name="emp_num", columnDefinition = "VARCHAR(255)")
    private String jobNumber;

    /**
     * 是否发送工资条
     */
    @Column(name="is_send", columnDefinition="BIT")
    private boolean isSend;

    /**
     * 用户是否已查看
     */
    @Column(name="is_read", columnDefinition="BIT")
    private boolean isRead;

    /**
     * 用户是否已确认
     */
    @Column(name="is_check", columnDefinition="BIT")
    private boolean isCheck;

    /**
     * 发送工资条时间
     */
    @Column(name="send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * 首次查看时间
     */
    @Column(name="read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    /**
     * 用户确认时间
     */
    @Column(name="check_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    /**
     * 实发金额
     */
    @Column(name="net_paid", columnDefinition="DECIMAL(10,2)")
    private BigDecimal netPaid;

    /**
     * 反馈
     */
    @Column(name="is_feedback", columnDefinition="BIT")
    private boolean isFeedBack;


    @Column(name="feedback_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime feedBackTime;

    @Column(name="feedback_content", columnDefinition="VARCHAR(1000)")
    private String feedBackContent;

    /**
     * 异常信息
     */
    @Transient
    private String error;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private SalaryMain salaryMain;


    /**
     * 使用静态创建，不要使用constructor
     * @param salaryMain 主体
     * @param jobNumber 工号
     * @return SalarySlip
     */
    public static SalarySlip create(SalaryMain salaryMain, String jobNumber, String rowId) {
        SalarySlip slip = new SalarySlip();
        slip.setMainId(salaryMain.getId());
        slip.setJobNumber(jobNumber);
        slip.setRowId(rowId);
        return slip;
    }

    public SalarySlip send() {
//        if (StringUtils.isNotBlank(this.error)) {
//            throw new BusinessException("数据存在异常不能发送工资条！异常原因: " + this.error);
//        }
        this.isSend = true;
        this.sendTime = LocalDateTime.now();
        return this;
    }

    public SalarySlip read() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
        return this;
    }

    public SalarySlip check() {
        this.isCheck = true;
        this.checkTime = LocalDateTime.now();
        return this;
    }

    public SalarySlip error(String error) {
        this.error = error;
        return this;
    }

    public SalarySlip correct() {
        this.error = null;
        return this;
    }






}