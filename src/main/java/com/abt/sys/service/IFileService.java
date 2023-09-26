package com.abt.sys.service;

import com.abt.sys.model.entity.UploadFile;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    void saveFile(MultipartFile file);
}
