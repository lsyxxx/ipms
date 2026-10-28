package com.abt.filecenter.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 文件中心 MinIO 配置，仅在启用 MinIO 时绑定和校验。
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "file-center.minio")
public class MinioProperties {

    @NotBlank(message = "MinIO 服务地址不能为空")
    @Pattern(regexp = "https?://[^\\s]+", message = "MinIO 服务地址必须使用 HTTP 或 HTTPS")
    private String endpoint;

    @NotBlank(message = "MinIO 访问密钥不能为空")
    private String accessKey;

    @NotBlank(message = "MinIO 私有密钥不能为空")
    private String secretKey;

    @NotBlank(message = "MinIO 存储桶不能为空")
    private String bucket;

    /** 单文件上限，单位字节；同时受应用 multipart 配置限制。 */
    @Min(value = 1, message = "MinIO 单文件大小上限必须大于零")
    private long maxSizeBytes = 200L * 1024 * 1024;
}
