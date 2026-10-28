package com.abt.filecenter.repository;

import com.abt.sys.exception.BusinessException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO 对象存储访问，不保存业务元数据。
 */
@Repository
@Slf4j
@ConditionalOnProperty(prefix = "file-center.minio", name = "enabled", havingValue = "true")
public class MinioFileRepository {

    private final MinioClient minioClient;

    public MinioFileRepository(@Qualifier("fileCenterMinioClient") MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String save(String bucket, String objectKey, MultipartFile file, String contentType) {
        try (InputStream stream = file.getInputStream()) {
            return minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(stream, file.getSize(), -1)
                    .contentType(contentType)
                    .build()).etag();
        } catch (Exception e) {
            log.error("MinIO 文件上传失败，bucket={}, objectKey={}", bucket, objectKey, e);
            throw new BusinessException("文件上传失败，请稍后重试或联系管理员");
        }
    }
}
