package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.model.RequestFile;
import com.abt.common.util.FileUtil;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.UploadFile;
import com.abt.sys.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
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

    @Value("com.abt.file.upload.save")
    private String savedRoot;

    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "上传文件")
    @Parameter(name = "form", description = "文件信息")
    @Parameter(name = "fileType", description = "附件类型")
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile[] files, @RequestParam RequestFile requestFile) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (files == null || files.length < 1) {
            log.warn("用户没有上传文件");
            return R.success(messages.getMessage("com.abt.sys.FileController.upload.empty"));
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                fileService.saveFile(user, file, requestFile);
            } catch (Exception e) {
                log.error("保存文件失败", e);
            }
        }

        return R.success();
    }
}
