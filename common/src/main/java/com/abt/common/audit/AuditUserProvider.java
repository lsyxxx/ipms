package com.abt.common.audit;

import java.util.Optional;

/**
 * 由应用模块提供当前用户
 */
public interface AuditUserProvider {

    /**
     * 当前操作人姓名
     */
    Optional<String> currentUsername();
}
