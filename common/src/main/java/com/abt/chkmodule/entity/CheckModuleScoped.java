package com.abt.chkmodule.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

/**
 * 需要使用checkModuleId时继承
 * 不能new
 */
@MappedSuperclass
public abstract class CheckModuleScoped {

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
