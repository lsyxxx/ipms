package com.abt.app.service.impl;

import com.abt.app.entity.AppVersion;
import com.abt.app.model.AppUpdateRequestForm;
import com.abt.app.respository.AppVersionRepository;
import com.abt.app.service.AppUpdateService;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.abt.app.entity.AppVersion.PLATFORM_ANDROID_APP;
import static com.abt.app.entity.AppVersion.PLATFORM_IOS_APP;

/**
 * app更新
 */
@Service
@Slf4j
public class AppUpdateServiceImpl implements AppUpdateService {
    @Value("${abt.app.bak.android.dir}")
    private String androidAppBakUrl;

    @Value("${abt.app.bak.ios.dir}")
    private String iosAppBakUrl;

    @Value("${abt.app.run.android.url}")
    private String runningAndroidApk;

    @Value("${abt.app.run.ios.url}")
    private String runningIosApk;

//    @Value("${abt.app.run.path}")
//    private String runDir;

    private final AppVersionRepository appVersionRepository;



    public AppUpdateServiceImpl(AppVersionRepository appVersionRepository) {
        this.appVersionRepository = appVersionRepository;
    }

    @Override
    public Page<AppVersion> findAppPaged(AppUpdateRequestForm form) {
        Pageable pageable = PageRequest.of(form.jpaPage(), form.getLimit());
        return appVersionRepository.findBy(form.getPlatform(), form.getVersion(), pageable);
    }

    @Override
    public void save(AppVersion appVersion) {
        appVersionRepository.save(appVersion);
    }

    @Override
    public void delete(String id) {
        appVersionRepository.deleteById(id);
    }

    public AppVersion getEntity(String id) {
        return appVersionRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到apk文件(entity.id=" + id + ")"));
    }

    private String fileNameGenerator() {
        return System.currentTimeMillis() + ".apk";
    }

    private String filePathGenerator(String platform) {
        return switch (platform) {
            case PLATFORM_ANDROID_APP -> androidAppBakUrl;
            case PLATFORM_IOS_APP -> iosAppBakUrl;
            default -> throw new BusinessException("未知的app平台类型" + platform);
        };
    }

    @Override
    public void uploadApk(MultipartFile file, String id, String platform) throws IOException {
        final AppVersion entity = getEntity(id);
        String fileName = this.fileNameGenerator();
        String url = this.filePathGenerator(platform);
        Objects.requireNonNull(file).transferTo(new File(url + fileName));
        entity.setUrl(url);
        entity.setFileName(fileName);
        entity.setFullUrl(url + fileName);

        appVersionRepository.save(entity);
    }

    /**
     * 设置当前版本为正在运行的
     * @param id
     * @throws IOException
     */
    public void setRunningApk(String id) throws IOException {
        AppVersion entity = this.getEntity(id);
        //获取备份
        File bak = new File(entity.getFullUrl());
        if (bak.exists()) {
            FileUtils.copyFile(bak, new File(getRunningUrl(entity)));
        } else {
            throw new BusinessException("该版本没有上传apk文件(id=" + id + ")");
        }
    }

    private String getRunningUrl(AppVersion appVersion) {
        return switch (appVersion.getPlatform()) {
            case PLATFORM_ANDROID_APP -> this.runningAndroidApk;
            case PLATFORM_IOS_APP -> this.runningIosApk;
            default -> throw new BusinessException("未知的app平台" + appVersion.getPlatform());
        };
    }

    /**
     * 获取正在运行的app版本
     * @return runningAppVersion
     */
    public AppVersion getRunningApp() {

        return null;
    }

    public static void main(String[] args) throws IOException {
        File bak = new File("E:\\05_app_bak\\test.apk");
        FileUtils.copyFile(bak, new File("E:\\05_app_bak\\run\\apk.apk"));
    }


}
