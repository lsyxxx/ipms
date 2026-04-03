package com.abt.db;

import com.abt.common.audit.AuditUserProvider;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * admin：从登录 Token 解析当前用户显示名，供 {@link com.abt.common.audit.AuditInfoListener} 写入用户名。
 */
@Component
public class TokenAuditUserProvider implements AuditUserProvider {

    @Override
    public Optional<String> currentUsername() {
        try {
            UserView u = TokenUtil.getUserFromAuthToken();
            if (u != null && StringUtils.hasText(u.getUsername())) {
                return Optional.of(u.getUsername());
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }
}
