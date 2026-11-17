package com.abt.testing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 关联的旧系统检测项目摘要，name 来自 T_checkModule.Fname。 */
@Getter
@AllArgsConstructor
public class ReportCoverCheckModuleVO {
    private final String id;
    /** 项目被外部删除时保留 ID，名称为 null。 */
    private final String name;
}
