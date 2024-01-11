package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.model.RequestFile;
import com.abt.common.model.ResCode;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/sys/file")
@Tag(name = "FileController", description = "文件相关")
public class FileController {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @Value("${com.abt.file.upload.save}")
    private String savedRoot;

    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "上传文件")
    @Parameter(name = "bizType", description = "业务类型")
    @Parameter(name = "service", description = "应用服务模块")
    @Parameter(name = "relationId1", description = "关联id1")
    @Parameter(name = "relationId2", description = "关联id2")
    @PostMapping("/upload")
    public R<List<String>> upload(@RequestParam("file") MultipartFile[] files,
                            @RequestParam String service,
                            @RequestParam(required = false) String bizType,
                            @RequestParam(required = false) String relationId1, @RequestParam(required = false) String relationId2) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (files == null || files.length < 1) {
            log.warn("用户没有上传文件");
            return R.success(messages.getMessage("com.abt.sys.FileController.upload.empty"));
        }
        String failed = null;
        String msg = null;
        List<String> fileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                final SystemFile systemFile = fileService.saveFile(user, file, create(bizType, service, relationId1, relationId2));
                fileIds.add(systemFile.getId());
            } catch (Exception e) {
                log.error("保存文件失败", e);
                if (failed != null) {
                    failed = " ," + failed + file.getName();
                } else {
                    failed = file.getName();
                }
                msg = MessageUtil.format("com.abt.sys.FileController.save.error", failed);
            }
        }

        return R.success(fileIds, fileIds.size(), msg == null ? ResCode.SUCCESS.getMessage() : msg);
    }

    @Operation(summary = "删除一个上传文件")
    @Parameter(name = "id", description = "文件id")
    @GetMapping("/del")
    public R delete(String id, @RequestParam(required = false) String name) {
        fileService.delete(id, name);
        return R.success();
    }

    @Operation(summary = "下载文件")
    @Parameter(name = "id", description = "文件id")
    @GetMapping("/download")
    public ResponseEntity<Resource> download(String id, @RequestParam(required = false) String name) {

        final SystemFile systemFile = fileService.findById(id, name);
        String url = systemFile.getUrl();
        File file = new File(url);

        // 设置HTTP响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", systemFile.getName());

        FileSystemResource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


    private RequestFile create(String bizType, String service, String relationId1, String relationId2) {
        RequestFile requestFile = new RequestFile();
        requestFile.setBizType(bizType);
        requestFile.setService(service);
        requestFile.setRelationId1(relationId1);
        requestFile.setRelationId2(relationId2);
        requestFile.setSavedRoot(savedRoot);
        return requestFile;
    }

}
