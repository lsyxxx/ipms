package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.util.FileUtil;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.service.IFileService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Value("${com.abt.file.upload.temp}")
    private String tempAccess;

//    @Value("${com.abt.file.web.root}")
    private static final String tempRoot = "E:\\质检OA附件记录";

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
            , @RequestParam("service") String service, @RequestParam(value = "withTime", required = false, defaultValue = "true") Boolean withTime) {
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
                SystemFile systemFile = fileService.saveFile(file, savedRoot, service, true, withTime);
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
    public R<Object> delete(@RequestParam String fullUrl) {
        log.info("=== 删除文件！fullUrl: {}", fullUrl);
        final boolean delete = fileService.delete(fullUrl);
        if (delete) {
            return R.success();
        } else {
            log.error("删除文件失败! {}", fullUrl);
            throw new BusinessException("删除文件失败!");
//            return R.fail("删除文件失败 - " + fullUrl);
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
    public ResponseEntity<byte[]> download(@RequestParam String url, @RequestParam String name) throws UnsupportedEncodingException {

        File file = new File(url);

        // 设置HTTP响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(name, "UTF-8"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败: ", e);
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * 复制文件到外网可访问的地址
     * todo: 需要定时清理临时文件
     * @param url 文件原始地址
     */
    @GetMapping("/copy/temp")
    public R<String> copyToTempFile(String url, String originalName) throws IOException {
        File file = new File(url);
        String rename = FileUtil.rename(originalName);
        String path = tempRoot + File.separator + tempAccess;
        File dir = new File(path);
        if (!FileUtils.isDirectory(dir)) {
            dir.mkdirs();
        }
        String newUrl = path + File.separator + rename;
        File tempFile = new File(newUrl);
        //复制文件到外网临时地址
        FileUtils.copyFile(file, tempFile);
        return R.success(tempAccess + File.separator + rename, "获取新的url");
    }


}
