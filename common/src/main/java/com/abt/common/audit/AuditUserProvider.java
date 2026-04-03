package com.abt.common.audit;

import java.util.Optional;

/**
 * admin和wxapp提供当前用户的方式不同
 */
public interface AuditUserProvider {

    /**
     * 当前操作人姓名
     */
    Optional<String> currentUsername();
}
