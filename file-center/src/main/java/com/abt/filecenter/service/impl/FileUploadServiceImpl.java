package com.abt.filecenter.service.impl;

import com.abt.filecenter.config.MinioProperties;
import com.abt.filecenter.model.FileUploadRequest;
import com.abt.filecenter.model.FileUploadResponse;
import com.abt.filecenter.repository.MinioFileRepository;
import com.abt.filecenter.service.FileUploadService;
import com.abt.sys.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * 文件上传校验与对象键生成，支持应用内其他业务复用。
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "file-center.minio", name = "enabled", havingValue = "true")
public class FileUploadServiceImpl implements FileUploadService {

    private final MinioFileRepository repository;
    private final MinioProperties properties;

    /** 校验文件大小和目录，生成唯一对象键并上传。 */
    @Override
    public FileUploadResponse save(FileUploadRequest request) {
        MultipartFile file = request == null ? null : request.getFile();
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择非空文件上传");
        }
        if (file.getSize() > properties.getMaxSizeBytes()) {
            throw new BusinessException("文件大小超过上传限制");
        }
        String directory = request.getDirectory();
        if (directory == null || !directory.matches("[A-Za-z0-9_-]{1,64}")) {
            throw new BusinessException("文件目录只能包含字母、数字、中划线和下划线，长度为1到64位");
        }
        String name = originalName(file);
        String extension = StringUtils.getFilenameExtension(name);
        // 仅保留安全的短扩展名；原始文件名不参与对象路径生成。
        String suffix = extension != null && extension.matches("[A-Za-z0-9]{1,16}")
                ? "." + extension.toLowerCase(Locale.ROOT) : "";
        String objectKey = directory + "/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "/" + UUID.randomUUID() + suffix;
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String etag = repository.save(properties.getBucket(), objectKey, file, contentType);
        log.info("MinIO 文件上传成功，bucket={}, objectKey={}, size={}", properties.getBucket(), objectKey, file.getSize());
        return FileUploadResponse.builder()
                .bucket(properties.getBucket()).objectKey(objectKey).originalName(name)
                .contentType(contentType).size(file.getSize()).etag(etag).build();
    }

    private String originalName(MultipartFile file) {
        String name = file.getOriginalFilename();
        name = name == null ? null : StringUtils.getFilename(name.replace('\\', '/'));
        return StringUtils.hasText(name) ? name : "file";
    }
}
