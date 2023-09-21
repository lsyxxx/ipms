package com.abt.sys.service;

import com.abt.sys.model.entity.UploadFile;
import jakarta.validation.constraints.NotNull;

public interface IFileService {
    void saveFile(UploadFile file);
}
