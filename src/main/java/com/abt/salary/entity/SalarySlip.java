package com.abt.salary.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sl_bill")
public class SalarySlip extends AuditInfo {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联SalaryMain id
     */
    @Column(name="m_id", columnDefinition="VARCHAR(255)", nullable = false)
    private String mid;

    /**
     * 关联SalaryDetail id
     */
    @Column(name="d_id", columnDefinition="VARCHAR(255)", nullable = false)
    private String did;

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
    private LocalDateTime sendTime;

    /**
     * 首次查看时间
     */
    @Column(name="read_time")
    private LocalDateTime readTime;

    /**
     * 用户确认时间
     */
    @Column(name="check_time")
    private LocalDateTime checkTime;




}