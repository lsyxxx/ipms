package com.abt.wf.exception;

import com.abt.common.model.ResCode;
import com.abt.sys.exception.BusinessException;

/**
 * 没有找到活动Task
 */
public class NoSuchActiveTaskException extends BusinessException {
    public NoSuchActiveTaskException(String message) {
        super(message, ResCode.FAIL.getCode());
    }
}
