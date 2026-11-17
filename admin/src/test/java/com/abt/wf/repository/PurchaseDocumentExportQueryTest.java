package com.abt.wf.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Converter;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** 不连接数据库，使用项目实体元数据和 SQL Server 方言验证导出查询。 */
class PurchaseDocumentExportQueryTest {
    @Test
    void validatesQueriesAgainstActualEntities() throws Exception {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect")
                .applySetting("hibernate.temp.use_jdbc_metadata_defaults", "false")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .applySetting("hibernate.jpa_callbacks.enabled", "false")
                .applySetting("hibernate.connection.provider_class", "org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl")
                .build();
        try {
            MetadataSources sources = new MetadataSources(registry);
            var scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
            scanner.addIncludeFilter(new AnnotationTypeFilter(Converter.class));
            for (var definition : scanner.findCandidateComponents("com.abt")) {
                sources.addAnnotatedClass(Class.forName(definition.getBeanClassName()));
            }
            try (SessionFactoryImplementor factory = (SessionFactoryImplementor) sources.buildMetadata().buildSessionFactory()) {
                for (var method : PurchaseApplyMainRepository.class.getDeclaredMethods()) {
                    if (List.of("findDocumentExportIds", "findDocumentExportBatch").contains(method.getName())) {
                        assertNotNull(factory.getQueryEngine().getHqlTranslator().translate(method.getAnnotation(Query.class).value(), null));
                    }
                }
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
