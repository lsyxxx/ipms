package com.abt.filecenter.service;

import com.abt.filecenter.model.FileUploadRequest;
import com.abt.filecenter.model.FileUploadResponse;

/**
 * 文件中心上传服务。
 */
public interface FileUploadService {

    /** 校验并上传单个文件，返回存储信息。 */
    FileUploadResponse save(FileUploadRequest request);
}
