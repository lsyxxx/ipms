package com.abt.salary.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;

/**
 * 明细数据
 */
@Getter
@Setter
@Entity
@Table(name = "sl_dtl")
public class SalaryDetail {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @ExcelIgnore
    private String id;

    /**
     * 主体id
     */
    @ExcelIgnore
    @Column(name="m_id")
    private String mainId;

    /**
     * 主体数据对象
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    @ExcelIgnore
    private SalaryMain mainData;

    /**
     * 序号
     */
    @ExcelProperty("序号")
    @Column(name="no_", columnDefinition="INT")
    private int no;

    /**
     * 员工姓名
     */
    @ExcelProperty("姓名")
    @NotNull
    @Column(name="name_", columnDefinition = "VARCHAR(32)")
    private String name;

    /**
     * 工号
     */
    @ExcelProperty("工号")
    @NotNull
    @Column(name="emp_num")
    private String jobNumber;

    @ExcelProperty("部门")
    @Column(name="dept_name")
    private String departmentName;

    /**
     * 岗位/职务
     */
//    @ExcelProperty(index = 6)
    @Column(name="position_", columnDefinition="VARCHAR(128)")
    private String position;
    /**
     * 基础工资-基本工资
     */
//    @ExcelProperty(index = 7)
    @Column(name="pay_base", columnDefinition="DECIMAL(10,2)")
    private BigDecimal payBase;
    /**
     * 基础工资-保密工资
     */
    @Column(name="pay_secrecy", columnDefinition="DECIMAL(10,2)")
    private BigDecimal paySecrecy;

    /**
     * 基础工资-岗位工资
     */
//    @ExcelProperty(index = 8)
    @Column(name="pay_position", columnDefinition="DECIMAL(10,2)")
    private BigDecimal payPosition;
    /**
     * 基础工资-绩效工资
     */
//    @ExcelProperty(index = 9)
    @Column(name="pay_merit", columnDefinition="DECIMAL(10,2)")
    private BigDecimal payMerit;

    /**
     * 基础工资合计
     */
    @NotNull
//    @ExcelProperty(index = 10)
    @Column(name="pay_base_sum", columnDefinition="DECIMAL(10,2)")
    private BigDecimal payBaseSum;
    /**
     * 出勤天数
     */
//    @ExcelProperty(index = 11)
    @Column(name="attendance_day", columnDefinition="DECIMAL(6,2)")
    private double attendanceDay;
    /**
     * 迟到早退次数
     */
//    @ExcelProperty(index = 12)
    @Column(name="late_count", columnDefinition="DECIMAL(6,2)")
    private double lateCount;
    /**
     * 病假天数
     */
//    @ExcelProperty(index = 13)
    @Column(name="sick_leave_day", columnDefinition="DECIMAL(6,2)")
    private double sickLeaveDay;

    /**
     * 事假天数
     */
//    @ExcelProperty(index = 14)
    @Column(name="person_leave_day", columnDefinition="DECIMAL(6,2)")
    private double personLeaveDay;
    /**
     * 旷工天数
     */
//    @ExcelProperty(index = 15)
    @Column(name="absence_day", columnDefinition="DECIMAL(6,2)")
    private double absenceDay;
    /**
     * 补发上月加班费
     */
//    @ExcelProperty(index = 16)
    @Column(name="pay_lm_ot", columnDefinition="DECIMAL(6,2)")
    private BigDecimal payLastMonthOvertime;
    /**
     * 应扣-病假
     */
//    @ExcelProperty(index = 17)
    @Column(name="de_sick_leave", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductSickLeave;
    /**
     * 应扣-事假
     */
//    @ExcelProperty(index = 18)
    @Column(name="de_person_leave", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductPersonLeave;
    /**
     * 应扣-旷工
     */
//    @ExcelProperty(index = 19)
    @Column(name="de_absence", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductAbsence;
    /**
     * 应扣-迟到早退
     */
//    @ExcelProperty(index = 20)
    @Column(name="de_late_early", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductLateAndEarly;
    /**
     * 出勤工资合计
     */
    @NotNull
//    @ExcelProperty(index = 21)
    @Column(name="pay_absence_sum", columnDefinition="DECIMAL(10,2)")
    private BigDecimal payAbsenceSum;
    /**
     * 补贴-餐补
     */
//    @ExcelProperty(index = 22)
    @Column(name="allowance_meal", columnDefinition="DECIMAL(10,2)")
    private BigDecimal allowanceMeal;
    /**
     * 补贴-通信费
     */
//    @ExcelProperty(index = 23)
    @Column(name="allowance_comm", columnDefinition="DECIMAL(10,2)")
    private BigDecimal allowanceCommunication;

    /**
     * 补贴-交通费
     */
//    @ExcelProperty(index = 24)
    @Column(name="allowance_trans", columnDefinition="DECIMAL(10,2)")
    private BigDecimal allowanceTransportation;
    /**
     * 补贴-生产奖金
     */
//    @ExcelProperty(index = 25)
    @Column(name="allowance_prod", columnDefinition="DECIMAL(10,2)")
    private BigDecimal allowanceProduction;

    /**
     * 补贴-提成
     */
//    @ExcelProperty(index = 26)
    @Column(name="allowance_commission", columnDefinition="DECIMAL(10,2)")
    private BigDecimal allowanceCommission;

    /**
     * 社保费(单位)-社保养老
     */
//    @ExcelProperty(index = 27)
    @Column(name="com_pay_social", columnDefinition="DECIMAL(10,2)")
    private BigDecimal companyPaySocial;
    /**
     * 社保费(单位)-四险
     */
//    @ExcelProperty(index = 28)
    @Column(name="com_pay_insure", columnDefinition="DECIMAL(10,2)")
    private BigDecimal companyPayInsurance;
    /**
     * 社保费(单位)-公积金
     */
//    @ExcelProperty(index = 29)
    @Column(name="com_pay_house", columnDefinition="DECIMAL(10,2)")
    private BigDecimal companyPayHouse;
    /**
     * 应发合计
     */
    @NotNull
//    @ExcelProperty(index = 30)
    @Column(name="pay_sum", columnDefinition="DECIMAL(10,2)")
    private BigDecimal paySum;
    /**
     * 月度扣款(个人)-社保养老
     */
//    @ExcelProperty(index = 31)
    @Column(name="de_social", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductSocial;
    /**
     * 月度扣款(个人)-四险
     */
//    @ExcelProperty(index = 31)
    @Column(name="de_insure", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductInsurance;
    /**
     * 月度扣款-社保补缴金额
     */
//    @ExcelProperty(index = 32)
    @Column(name="de_backpay_social", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductBackPaySocial;
    /**
     * 月度扣款-公积金
     */
//    @ExcelProperty(index = 33)
    @Column(name="de_house", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductHouse;
    /**
     * 月度扣款-税费
     */
//    @ExcelProperty(index = 34)
    @Column(name="de_tax", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductTax;
    /**
     * 罚款
     */
//    @ExcelProperty(index = 35)
    @Column(name="penalty", columnDefinition="DECIMAL(10,2)")
    private BigDecimal penalty;
    /**
     * 暂扣奖金
     */
//    @ExcelProperty(index = 36)
    @Column(name="de_temp_bonus", columnDefinition="DECIMAL(10,2)")
    private BigDecimal deductTempBonus;
    /**
     * 代扣代缴
     */
//    @ExcelProperty(index = 37)
    @Column(name="other_pay", columnDefinition="DECIMAL(10,2)")
    private BigDecimal otherPay;
    /**
     * 本月实发
     */
    @NotNull
//    @ExcelProperty(index = 38)
    @Column(name="net_paid", columnDefinition="DECIMAL(10,2)")
    private BigDecimal netPaid;

    /**
     * 备注信息
     */
//    @ExcelProperty(index = 39)
    @Column(name="remark_", columnDefinition="VARCHAR(1000)")
    private String remark;




}