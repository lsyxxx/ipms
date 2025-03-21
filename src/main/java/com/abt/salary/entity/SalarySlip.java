package com.abt.salary.entity;

import com.abt.common.listener.JpaListStringConverter;
import com.abt.common.model.AuditInfo;
import com.abt.common.model.User;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.wf.entity.UserSignature;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Slf4j
public class SalarySlip extends AuditInfo{
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * excel中的序号
     */
    @Column(name="idx_xlsx")
    private Integer indexExcel;

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
     * 上传的excel上的部门，可能是employee表中不同
     */
    @Column(name="dept_xlsx")
    private String deptExcel;

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
     * 部门审核人工号
     */
    @Column(name="dm_jno")
    private String dmJobNumber;
    @Column(name="dm_name")
    private String dmName;
    @Column(name="dm_time")
    private LocalDateTime dmTime;
    @Transient
    private String dmSig;

    /**
     * 副总审核工号
     */
    @Column(name="dceo_jno")
    private String dceoJobNumber;
    @Column(name="dceo_name")
    private String dceoName;
    @Column(name="dceo_time")
    private LocalDateTime dceoTime;
    @Transient
    private String dceoSig;

    /**
     * 总经理审批
     */
    @Column(name="ceo_jno")
    private String ceoJobNumber;
    @Column(name="ceo_name")
    private String ceoName;
    @Column(name="ceo_time")
    private LocalDateTime ceoTime;
    @Transient
    private String ceoSig;


    /**
     * 人事审批
     */
    @Column(name="hr_jno")
    private String hrJobNumber;
    @Column(name="hr_name")
    private String hrName;
    @Column(name="hr_time")
    private LocalDateTime hrTime;
    @Transient
    private String hrSig;

    /**
     * 异常信息
     */
    @Column(name="error_", columnDefinition = "VARCHAR(MAX)")
    @Convert(converter = JpaListStringConverter.class)
    private List<String> error;

    public boolean isError() {
        return error != null && !error.isEmpty();
    }

    public void addError(String error) {
        if (this.error == null) {
            this.error = new ArrayList<>();
        }
        this.error.add(error);
    }

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
     * 用户签名
     */
    @Transient
    private String userSig;

    /**
     * 是否需要个人确认
     */
    @Column(name="force_check", columnDefinition = "BIT")
    private boolean isForceCheck;

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

    public static final String LABEL_USER_CHECK = "是否确认";
    public static final String LABEL_DEPT = "部门";
    public static final String LABEL_NAME = "姓名";
    public static final String LABEL_JOB_NUMBER = "工号";
    public static final String LABEL_INDEX = "序号";

    /**
     * 是否强制签名确认
     */
    private boolean translateUserCheck(String value) {
        this.setForceCheck("是".equals(value) || StringUtils.isBlank(value));
        return this.isForceCheck;
    }

    /**
     * 使用静态创建，不要使用constructor。
     * 创建时包含部门/是否确认
     * @param salaryMain 主体
     * @return SalarySlip
     */
    public static SalarySlip create(SalaryMain salaryMain, List<SalaryCell> row) {
        if (row == null || row.isEmpty()) {
            return null;
        }
        SalarySlip slip = new SalarySlip();
        //必须有的数据
        slip.setMainId(salaryMain.getId());
        for (SalaryCell cell : row) {
            slip.setName(row.get(0).getName());
            slip.setYearMonth(row.get(0).getYearMonth());
            slip.setJobNumber(row.get(0).getJobNumber());
            if (LABEL_INDEX.equals(cell.getLabel())) {
                try {
                    slip.setIndexExcel(Integer.parseInt(cell.getValue()));
                } catch (NumberFormatException e) {
                    log.warn("NumberFormatException:", e);
                }
            }
            if (LABEL_DEPT.equals(cell.getLabel())) {
                slip.setDeptExcel(cell.getValue());
            }
            if (LABEL_USER_CHECK.equals(cell.getLabel())) {
                slip.translateUserCheck(cell.getValue());
            }
        }

        return slip;
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

    public boolean isDmCheck() {
        return this.dmTime != null;
    }

    public boolean isDceoCheck() {
        return this.dceoTime != null;
    }

    public boolean isCeoCheck() {
        return this.ceoTime != null;
    }

    public boolean isHrCheck() {
        return this.hrTime != null;
    }

    public boolean needDmCheck() {
        return StringUtils.isNotBlank(this.dmJobNumber);
    }

    public boolean needDceoCheck() {
        return StringUtils.isNotBlank(this.dceoJobNumber);
    }
    public boolean needHrCheck() {
        return StringUtils.isNotBlank(this.hrJobNumber);
    }
    public boolean needCeoCheck() {
        return StringUtils.isNotBlank(this.ceoJobNumber);
    }

    @Transient
    private String curLeaderSig;
}