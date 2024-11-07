package com.abt.app.controller;

import com.abt.app.entity.AppVersion;
import com.abt.app.model.AppUpdateRequestForm;
import com.abt.app.service.AppUpdateService;
import com.abt.common.model.R;
import com.abt.common.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/app/version")
public class AppUpdateController {

    private final AppUpdateService appUpdateService;

    public AppUpdateController(AppUpdateService appUpdateService) {
        this.appUpdateService = appUpdateService;
    }

    @GetMapping("/list")
    public R<List<AppVersion>> appList(@ModelAttribute AppUpdateRequestForm form) {
        final Page<AppVersion> appPaged = appUpdateService.findAppPaged(form);
        return R.success(appPaged.getContent(), (int)appPaged.getTotalElements());
    }

    @PostMapping("/save")
    public R<Object> save(@RequestBody AppVersion appVersion) {
        appUpdateService.save(appVersion);
        return R.success("添加成功!");
    }

    @PostMapping("/update")
    public R<Object> update(@RequestBody AppVersion appVersion) {
        ValidateUtil.ensurePropertyNotnull(appVersion.getId(), "id");
        appUpdateService.save(appVersion);
        return R.success("更新成功!");
    }

    @PostMapping("/upload")
    public R<Object> uploadApk(@RequestParam("file") MultipartFile file, @RequestParam("id") String id) throws IOException {
//        appUpdateService.uploadApk(file, id);
        return R.success("上传成功!");
    }

    @GetMapping("/del")
    public R<Object> delete(@RequestParam("id") String id) {
        appUpdateService.delete(id);
        return R.success("删除成功!");
    }

    /**
     * 设置为当前正在运行的版本
     * @param id 实体id
     */
//    @GetMapping("/run")
//    public R<Object> runningVersion(String id) {
//
//    }



}
