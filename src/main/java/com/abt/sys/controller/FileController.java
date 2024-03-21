package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/sys/file")
public class FileController {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @Value("${com.abt.file.upload.save}")
    private String savedRoot;

    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }


    /**
     * 上传文件，但不保存文件信息到数据库。仅上传目录
     * @param files 上传的文件
     * @param service 文件所属服务
     */
    @PostMapping("/upload")
    public R<List<SystemFile>> upload(@RequestParam("file") MultipartFile[] files
            , @RequestParam("service") String service) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (files == null || files.length < 1) {
            log.warn("用户没有上传文件");
            return R.noFileUpload();
        }
        String failed = null;
        String msg = null;
        List<SystemFile> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                SystemFile systemFile = fileService.saveFile(file, savedRoot, service, true);
                saved.add(systemFile);
            } catch (Exception e) {
                log.error("保存文件失败", e);
                if (failed != null) {
                    failed = " ," + failed + file.getOriginalFilename();
                } else {
                    failed = file.getName();
                }
                msg = MessageUtil.format("com.abt.sys.FileController.save.error", failed);
            }
        }

        return R.success(saved, saved.size(), msg);
    }

    /**
     * 删除单个文件
     * @param fullUrl 文件完整地址
     */
    @GetMapping("/del")
    public R delete(@RequestParam String fullUrl) {
        final boolean delete = fileService.delete(fullUrl);
        if (delete) {
            return R.success();
        } else {
            return R.fail("删除文件失败 - " + fullUrl);
        }
    }


    /**
     * 删除多个为念
     * @param urlList 文件完整地址集合
     */
    public R deleteAll(@RequestParam String[] urlList) {
        StringBuilder msg = new StringBuilder();
        for (String url : urlList) {
            final boolean delete = fileService.delete(url);
            if (!delete) {
                msg.append(url).append(" 删除失败");
            }
        }
        if (msg.isEmpty()) {
            return R.success("删除文件成功");
        }
        return R.success(msg);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam String url, @RequestParam String name) throws UnsupportedEncodingException, FileNotFoundException {

        FileInputStream fis = new FileInputStream(url);
        InputStreamResource iss = new InputStreamResource(fis);

        // 设置HTTP响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(iss);
    }


}
