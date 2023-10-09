package com.abt.sys.service.impl;

import com.abt.common.model.RequestFile;
import com.abt.common.util.FileUtil;
import com.abt.common.util.MessageUtil;
import com.abt.sys.exception.SystemFileNotFoundException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.repository.SystemFileRepository;
import com.abt.sys.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 文件处理
 */
@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    private final SystemFileRepository systemFileRepository;

    public FileServiceImpl(SystemFileRepository systemFileRepository) {
        this.systemFileRepository = systemFileRepository;
    }

    @Override
    public void saveFile(UserView user, MultipartFile file, RequestFile requestFile) {
        log.info("开始执行saveFile()...");
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

    @Override
    public void delete(String id, String name) {
        log.info("开始执行delete()...");
        final Optional<SystemFile> sysFile = systemFileRepository.findById(id);

        findFileException(sysFile, id, name);

        systemFileRepository.softDelete(id);
    }

    @Override
    public SystemFile findById(String id, String name) {
        final Optional<SystemFile> byId = systemFileRepository.findById(id);
        findFileException(byId, id, name);
        return byId.get();
    }

    private void findFileException(Optional<SystemFile> systemFile, String id, String name) {
        if (systemFile.isEmpty()) {
            log.error("未找到需要删除的文件, 文件id: {}, name: {}", id, name);
            throw new SystemFileNotFoundException(MessageUtil.format("com.abt.sys.FileController.delete.noFile", name));
        }
    }

}
