package com.abt.common.audit;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * 将 Spring 容器中的 {@link AuditUserProvider} 注册到 {@link AuditInfoListener}。
 */
@Configuration
@ConditionalOnClass(AuditInfoListener.class)
public class AuditUserProviderRegistrar {

    private final ObjectProvider<AuditUserProvider> auditUserProvider;

    public AuditUserProviderRegistrar(ObjectProvider<AuditUserProvider> auditUserProvider) {
        this.auditUserProvider = auditUserProvider;
    }

    @PostConstruct
    void register() {
        auditUserProvider.ifAvailable(AuditInfoListener::setProvider);
    }
}
