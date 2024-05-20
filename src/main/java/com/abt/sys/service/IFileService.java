package com.abt.sys.service;

import com.abt.sys.model.entity.SystemFile;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    SystemFile saveFile(MultipartFile file, String filePath, String service, boolean isRename, Boolean withTime);

    boolean delete(String fullUrl);
}
