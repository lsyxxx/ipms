package com.abt.sys.service;

import com.abt.common.model.RequestFile;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.model.entity.UploadFile;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    void saveFile(UserView user, MultipartFile file, RequestFile requestFile);

    List<SystemFile> findBy(SystemFile condition);

    void delete(String id, String name);

    SystemFile findById(String id, String name);
}
