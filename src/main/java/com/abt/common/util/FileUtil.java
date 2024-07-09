package com.abt.common.util;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemFile;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 *
 */
@Slf4j
public class FileUtil {


    public static String uploadPath(String root, @NotNull String fileName) {
        return root + File.separator + fileName;
    }

    public static String getFileExtension(@NotNull String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public static boolean deleteFile(String path) {
        Assert.hasLength(path, "文件路径不能为空");
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 重命名文件，防止文件名重复
     * @param originalFileName 原文件名，带后缀
     */
    public static String rename(String originalFileName) {
        return TimeUtil.idGenerator() + originalFileName;
    }


    public static SystemFile copyFile(File file, String originalName, String service, boolean isRename, Boolean withTime, String savedRoot) {
        SystemFile systemFile = new SystemFile(originalName,  service, savedRoot, withTime);
        final String newName = FileUtil.copyFile(file, systemFile.getOriginalName(), systemFile.getUrl(), isRename);
        systemFile.rename(newName);
        return systemFile;
    }

    public static String copyFile(File file, String originalName, String path, boolean isRename) {
        if (file == null) {
            return "";
        }
        String fileName = originalName;
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (isRename) {
            fileName = rename(fileName);
        }
        File dest = new File(directory, fileName);
        try {
            FileUtils.copyFile(file, dest);
            return fileName;
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new BusinessException(e);
        }
    }


    public static String saveFile(MultipartFile file, String path, boolean isRename) {
        if (file == null) {
            return "";
        }
        String fileName = file.getOriginalFilename();
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (isRename) {
            fileName = rename(fileName);
        }
        File dest = new File(directory, fileName);
        try {
            file.transferTo(dest);
            return fileName;
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new BusinessException(e);
        }
    }

    public static void saveFile(MultipartFile file, String path, String newName) {
        if (file == null) {
            return ;
        }
        String fileName = file.getOriginalFilename();
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File dest = new File(directory, newName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new BusinessException(e);
        }
    }
}
