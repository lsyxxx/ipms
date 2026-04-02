package com.abt.chkmodule.entity;

import com.abt.common.AuditInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;

/**
 * 带审计AuditInfo和CheckModuleRef
 * 需要审计信息的实体类用
 */
@MappedSuperclass
public abstract class AuditedCheckModuleEntity extends AuditInfo {

    @Embedded
    @JsonUnwrapped
    protected CheckModuleRef checkModuleRef = new CheckModuleRef();

    public String getCheckModuleId() {
        return checkModuleRef != null ? checkModuleRef.getCheckModuleId() : null;
    }

    public void setCheckModuleId(String checkModuleId) {
        if (checkModuleRef == null) {
            checkModuleRef = new CheckModuleRef();
        }
        checkModuleRef.setCheckModuleId(checkModuleId);
    }
}
