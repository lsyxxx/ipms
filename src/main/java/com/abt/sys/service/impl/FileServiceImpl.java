package com.abt.sys.service.impl;

import com.abt.common.model.RequestFile;
import com.abt.common.util.FileUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.repository.SystemFileRepository;
import com.abt.sys.service.IFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件处理
 */
@Service
public class FileServiceImpl implements IFileService {

    private final SystemFileRepository systemFileRepository;

    public FileServiceImpl(SystemFileRepository systemFileRepository) {
        this.systemFileRepository = systemFileRepository;
    }

    @Override
    public void saveFile(UserView user, MultipartFile file, RequestFile requestFile) {
        if (StringUtils.isBlank(requestFile.getFileName())) {
            requestFile.setFileName(file.getOriginalFilename());
        }
        SystemFile systemFile = new SystemFile(requestFile);
        systemFile.setOriginalName(file.getOriginalFilename());
        systemFile.setUrl(FileUtil.getFileExtension(requestFile.getFileName()));

        FileUtil.saveFile(file, requestFile.getSavedRoot());
        systemFileRepository.save(systemFile);
    }

    @Override
    public List<SystemFile> findBy(SystemFile condition) {
        Example<SystemFile> example = Example.of(condition);
        return systemFileRepository.findAll(example);
    }


}
