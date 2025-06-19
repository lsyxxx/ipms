package com.abt.sys.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
}
