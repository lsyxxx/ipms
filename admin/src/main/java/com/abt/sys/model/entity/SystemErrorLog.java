package com.abt.sys.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 系统错误记录
 */
@Table(name = "sys_err_log")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({AuditingEntityListener.class})
public class SystemErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 错误信息
     */
    @Column(name="error_", length = 1000)
    private String error;

    /**
     * 业务类型
     */
    @Column(name="service_")
    private String service;

    /**
     * 问题是否处理完成/已解决
     */
    @Column(name="is_solved")
    private boolean isSolved = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    @Column(name="create_time")
    private LocalDateTime createTime;
    @CreatedBy
    @Column(name="create_userid")
    private String createUserid;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_time")
    private LocalDateTime updateTime;

    @LastModifiedBy
    @Column(name="update_userid")
    private String updateUserid;

}
