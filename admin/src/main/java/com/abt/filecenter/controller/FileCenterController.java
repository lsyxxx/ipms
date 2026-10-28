package com.abt.filecenter.controller;

import com.abt.common.model.R;
import com.abt.filecenter.model.FileUploadRequest;
import com.abt.filecenter.model.FileUploadResponse;
import com.abt.filecenter.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件中心上传接口，沿用 IPMS 登录认证和统一返回结构。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/file-center")
@ConditionalOnProperty(prefix = "file-center.minio", name = "enabled", havingValue = "true")
public class FileCenterController {

    private final FileUploadService fileUploadService;

    /** 上传单个文件到文件中心的 MinIO 存储。 */
    @Operation(summary = "上传文件到MinIO", operationId = "admin.fileCenter.upload")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<FileUploadResponse> save(@Valid @ModelAttribute FileUploadRequest request) {
        return R.success(fileUploadService.save(request), "上传成功");
    }
}
