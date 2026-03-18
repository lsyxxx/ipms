package com.abt.wxapp.sys.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


/**
 * 系统配置参数表
 */
@Getter
@Setter
@Entity
@Table(name = "T_SystemSetting")
public class SystemSetting {
    /**
     * 系统配置参数id
     */
    @Id
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    /**
     * 系统配置参数说明
     */
    @Column(name = "ftypeid", nullable = false, length = 50)
    private String name;

    /**
     * 参数值
     */
    @Column(name = "Fvalue", length = 30)
    private String value;


}