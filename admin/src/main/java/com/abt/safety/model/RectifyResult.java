package com.abt.safety.model;

import org.apache.commons.lang3.StringUtils;

/**
 * 整改复查结果
 */
public enum RectifyResult {
    pass,
    reject,
    ;

    public static boolean validate(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        return name.toLowerCase().equals(pass.name()) ||  name.toLowerCase().equals(reject.name());
    }

}
