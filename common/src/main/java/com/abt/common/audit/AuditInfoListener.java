package com.abt.common.audit;

import com.abt.common.AuditInfo;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.util.StringUtils;

/**
 * 在 {@link AuditingEntityListener} 写入 userid 之后，补充 username 字段。
 * 通过 {@link AuditUserProviderRegistrar} 注入各应用实现的 {@link AuditUserProvider}。
 */
public class AuditInfoListener {

    private static volatile AuditUserProvider provider;

    public static void setProvider(AuditUserProvider p) {
        AuditInfoListener.provider = p;
    }

    @PrePersist
    public void beforePersist(AuditInfo entity) {
        if (provider == null) {
            return;
        }
        var opt = provider.currentUsername();
        if (opt.isEmpty()) {
            return;
        }
        String name = opt.get();
        if (StringUtils.hasText(entity.getCreateUserid())) {
            entity.setCreateUsername(name);
        }
        if (StringUtils.hasText(entity.getUpdateUserid())) {
            entity.setUpdateUsername(name);
        }
    }

    @PreUpdate
    public void beforeUpdate(AuditInfo entity) {
        if (provider == null) {
            return;
        }
        var opt = provider.currentUsername();
        if (opt.isEmpty()) {
            return;
        }
        if (StringUtils.hasText(entity.getUpdateUserid())) {
            entity.setUpdateUsername(opt.get());
        }
    }
}
