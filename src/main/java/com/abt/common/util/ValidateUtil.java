package com.abt.common.util;

import com.abt.common.exception.MissingRequiredParameterException;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 */
public class ValidateUtil {

    public static void ensurePropertyNotnull(String propertyValue, String propertyName) {
        if (StringUtils.isBlank(propertyValue)) {
            throw new MissingRequiredParameterException(propertyName);
        }
    }
}
