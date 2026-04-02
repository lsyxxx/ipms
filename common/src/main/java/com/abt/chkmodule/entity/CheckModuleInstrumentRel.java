package com.abt.chkmodule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 检测项目-设备关联表
 */
@Getter
@Setter
@Entity
@Table(name = "check_module_instru")
public class CheckModuleInstrumentRel extends CheckModuleScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "instru_id", nullable = false)
    private String instrumentId;
}
