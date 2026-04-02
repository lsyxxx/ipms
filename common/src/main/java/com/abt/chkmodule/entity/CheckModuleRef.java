package com.abt.chkmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 统一checkModuleId的定义，防止多表使用时定义混乱
 * 引用的实体类要加外键限制
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckModuleRef {

    @Column(name = "cm_id", nullable = false, length = 128)
    private String checkModuleId;
}
