package com.abt.sys.service.impl;

import com.abt.sys.model.entity.UploadFile;
import com.abt.sys.repository.UploadFileRepository;
import com.abt.sys.service.IFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理
 */
@Service
public class FileServiceImpl implements IFileService {

    private final UploadFileRepository uploadFileRepository;

    public FileServiceImpl(UploadFileRepository uploadFileRepository) {
        this.uploadFileRepository = uploadFileRepository;
    }

    @Override
    public void saveFile(MultipartFile file) {
        UploadFile uploadFile = new UploadFile();



        uploadFileRepository.save(uploadFile);


    }


}
