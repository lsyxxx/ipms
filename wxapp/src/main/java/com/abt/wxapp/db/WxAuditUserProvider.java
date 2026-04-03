package com.abt.wxapp.db;

import com.abt.common.audit.AuditUserProvider;
import com.abt.wxapp.security.SecurityUtil;
import com.abt.wxapp.security.WxUserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * wxapp：从 SecurityContext 解析当前用户，供 {@link com.abt.common.audit.AuditInfoListener} 写入用户名。
 */
@Component
public class WxAuditUserProvider implements AuditUserProvider {

    @Override
    public Optional<String> currentUsername() {
        Optional<WxUserDetails> opt = SecurityUtil.currentUser();
        return opt.map(WxUserDetails::getDisplayNameForAudit);
    }
}
