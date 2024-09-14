package com.abt.app.service;

import com.abt.app.entity.AppVersion;
import com.abt.app.model.AppUpdateRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AppUpdateService {
    Page<AppVersion> findAppPaged(AppUpdateRequestForm form);

    void save(AppVersion appVersion);

    void delete(String id);

    void uploadApk(MultipartFile file, String id) throws IOException;
}
