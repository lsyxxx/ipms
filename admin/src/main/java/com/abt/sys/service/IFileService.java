package com.abt.sys.service;

import com.abt.sys.model.SystemFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IFileService {
    SystemFile saveFile(MultipartFile file, String filePath, String service, boolean isRename, Boolean withTime);

    SystemFile copyFile(File file, String originalName, String service, boolean isRename, Boolean withTime);

    boolean delete(String fullUrl);
}
