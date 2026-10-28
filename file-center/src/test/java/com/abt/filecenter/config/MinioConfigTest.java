package com.abt.filecenter.config;

import com.abt.filecenter.repository.MinioFileRepository;
import com.abt.filecenter.service.FileUploadService;
import com.abt.filecenter.service.impl.FileUploadServiceImpl;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MinIO 开关和配置校验测试，不发起存储服务请求。
 */
class MinioConfigTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(MinioConfig.class, MinioFileRepository.class, FileUploadServiceImpl.class);

    @Test
    void disabledByDefaultWithoutCredentials() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(MinioClient.class);
            assertThat(context).doesNotHaveBean(FileUploadService.class);
        });
    }

    @Test
    void enabledWithValidConfigurationWithoutContactingMinio() {
        runner.withPropertyValues("file-center.minio.enabled=true",
                "file-center.minio.endpoint=http://localhost:9000",
                "file-center.minio.access-key=test-access",
                "file-center.minio.secret-key=test-secret",
                "file-center.minio.bucket=ipms-test")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(MinioClient.class);
                    assertThat(context).hasSingleBean(FileUploadService.class);
                });
    }

    @Test
    void failsFastWhenEnabledWithoutCredentials() {
        runner.withPropertyValues("file-center.minio.enabled=true")
                .run(context -> assertThat(context).hasFailed());
    }
}
