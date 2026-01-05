package com.abt.sys.service.impl;

import com.abt.common.util.FileUtil;
import com.abt.common.util.MessageUtil;
import com.abt.sys.exception.SystemFileNotFoundException;
import com.abt.sys.model.SystemFile;
import com.abt.sys.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

/**
 * 文件处理
 */
@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @Value("${com.abt.file.upload.save}")
    private String savedRoot;



    /**
     * 仅保存文件到服务器，不保存信息到数据库
     * @param file 文件
     * @param filePath 公共目录
     * @return SystemFile 保存文件的信息
     */
    @Override
    public SystemFile saveFile(MultipartFile file, String filePath, String service, boolean isRename, Boolean withTime) {
        SystemFile systemFile = new SystemFile(file, service, filePath, withTime);
        final String newName = FileUtil.saveFile(file, systemFile.getUrl(), isRename);
        systemFile.rename(newName);
        log.info("已保存文件：{}, {}", systemFile.getOriginalName(), systemFile.getFullPath());
        return systemFile;
    }

    @Override
    public SystemFile copyFile(File file, String originalName, String service, boolean isRename, Boolean withTime) {
        SystemFile systemFile = new SystemFile(originalName,  service, savedRoot, withTime);
        final String newName = FileUtil.copyFile(file, systemFile.getOriginalName(), systemFile.getUrl(), isRename);
        systemFile.rename(newName);
        return systemFile;
    }




    @Override
    public boolean delete(String fullUrl) {
        log.info("开始执行delete()... fullUrl: {}", fullUrl);
        return FileUtil.deleteFile(fullUrl);
    }

    private void findFileException(Optional<SystemFile> systemFile, String id, String name) {
        if (systemFile.isEmpty()) {
            log.error("未找到需要删除的文件, 文件id: {}, name: {}", id, name);
            throw new SystemFileNotFoundException(MessageUtil.format("com.abt.sys.FileController.delete.noFile", name));
        }
    }


}
