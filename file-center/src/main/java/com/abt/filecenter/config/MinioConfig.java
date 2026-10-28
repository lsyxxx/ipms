package com.abt.filecenter.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件中心 MinIO 客户端配置，不在启动时创建桶或修改桶策略。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "file-center.minio", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Bean("fileCenterMinioClient")
    public MinioClient fileCenterMinioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}
