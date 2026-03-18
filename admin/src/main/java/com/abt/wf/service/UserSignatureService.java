package com.abt.wf.service;

import com.abt.wf.entity.UserSignature;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserSignatureService {
    //根据员工表查询员工的us
    List<UserSignature> getAllUserSignatures();

    String createFilePath(String fileName);

    UserSignature saveSignature(String jobNumber, String username, String company) throws IOException;

    UserSignature saveSignature(UserSignature us, String fullPath) throws IOException;

    String saveFile(MultipartFile file, String jobNumber, String username, String company) throws IOException;

    UserSignature findByJobNumber(String jobNumber);

    void deleteUserSignatureByJobNumber(String jobNumber);

    void saveOrUpdate(UserSignature us);
}
