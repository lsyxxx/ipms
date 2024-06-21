package com.abt.salary.model;

import com.abt.salary.entity.SalarySlip;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 *
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSlip {

    private String id;
    private String mid;
    private String yearMonth;
    private String name;
    private String jobNumber;
    private String departmentName;
    private String position;
    private boolean isRead;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime readTime;
    private boolean isCheck;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime checkTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime sendTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime autoCheckTime;

    public static UserSlip createBy(SalarySlip salarySlip) {
        UserSlip us = new UserSlip();
        us.setId(salarySlip.getId());
        us.setMid(salarySlip.getMainId());
        us.setYearMonth(salarySlip.getYearMonth());
        us.setName(salarySlip.getName());
        us.setJobNumber(salarySlip.getJobNumber());
        salarySlip.user();
        us.setDepartmentName(salarySlip.getUser().getDeptName());
        us.setPosition(salarySlip.getUser().getPosition());
        us.setRead(salarySlip.isRead());
        us.setReadTime(salarySlip.getReadTime());
        us.setCheck(salarySlip.isCheck());
        us.setCheckTime(salarySlip.getCheckTime());
        us.setSendTime(salarySlip.getSendTime());
        us.setAutoCheckTime(salarySlip.getAutoCheckTime());
        return us;
    }
}
