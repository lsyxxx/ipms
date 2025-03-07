package com.abt.salary.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.model.User;
import com.abt.sys.model.entity.EmployeeInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 */
@Getter
@Setter
@Entity
@Table(name = "sl_slip", indexes = {
        @Index(name = "idx_mid", columnList = "mid"),
        @Index(name = "idx_emp_num", columnList = "emp_num"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SalarySlip extends AuditInfo {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联SalaryMain id
     */
    @Column(name="mid", columnDefinition="VARCHAR(255)", nullable = false)
    private String mainId;

    @Column(name="emp_num", columnDefinition = "VARCHAR(255)")
    private String jobNumber;

    @Size(max = 32)
    @Column(name = "name_", columnDefinition = "VARCHAR(32)")
    private String name;

    /**
     * 工资年月: yyyy-MM
     */
    @NotNull(message = "工资发放年月不能为空")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "选择工资发放年月必须是yyyy-MM格式")
    @Column(name="year_mon", columnDefinition = "VARCHAR(32)")
    private String yearMonth;

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
     * 自动确认时间
     */
    @Column(name="auto_check_time")
    private LocalDateTime autoCheckTime;

    /**
     * 确认类型，手动/自动
     */
    @Column(name="check_type", columnDefinition = "VARCHAR(12)")
    private String checkType;
    public static final String CHECK_TYPE_AUTO = "auto";
    public static final String CHECK_TYPE_MANUAL = "manual";

    /**
     * 异常信息
     */
    @Transient
    private String error;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private SalaryMain salaryMain;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_num", referencedColumnName = "JobNumber", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private EmployeeInfo employeeInfo;

    @Transient
    private User user;

    /**
     * 生成user信息，必须获取employee等对象
     * 没有userid
     */
    public void user() {
        User user = new User();
        if (this.employeeInfo != null) {
            user.setUsername(this.employeeInfo.getName());
            user.setCode(this.employeeInfo.getJobNumber());
            user.setPosition(employeeInfo.getPosition());
            user.setDeptId(this.employeeInfo.getDept());
            if (this.employeeInfo.getDepartment() != null) {
                user.setDeptName(this.employeeInfo.getDepartment().getName());
            }
        }
        setUser(user);
    }


    /**
     * 使用静态创建，不要使用constructor
     * @param salaryMain 主体
     * @param jobNumber 工号
     * @return SalarySlip
     */
    public static SalarySlip create(SalaryMain salaryMain, String jobNumber) {
        SalarySlip slip = new SalarySlip();
        slip.setId(UUID.randomUUID().toString());
        slip.setMainId(salaryMain.getId());
        slip.setJobNumber(jobNumber);
        return slip;
    }

    public SalarySlip send() {
        this.isSend = true;
        this.sendTime = LocalDateTime.now();
        return this;
    }

    public SalarySlip send(LocalDateTime sendTime) {
        this.isSend = true;
        this.sendTime = sendTime;
        return this;
    }

    public SalarySlip read() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
        return this;
    }

    public SalarySlip check(String checkType) {
        this.isCheck = true;
        this.checkTime = LocalDateTime.now();
        this.checkType = checkType;
        return this;
    }

    public SalarySlip error(String error) {
        this.error = error;
        return this;
    }

}