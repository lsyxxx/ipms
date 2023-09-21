package com.abt.common.util;

import com.abt.sys.exception.BusinessException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
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

    public static void saveFile(MultipartFile file, String savedRoot) {
        if (file == null) {
            return;
        }

        String fileName = file.getOriginalFilename();

        String filePath = uploadPath(savedRoot, fileName);
        File dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new BusinessException(e);
        }
    }
}
