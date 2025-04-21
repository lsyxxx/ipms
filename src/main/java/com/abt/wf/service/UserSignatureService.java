package com.abt.wf.service;

import com.abt.wf.entity.UserSignature;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserSignatureService {
    //根据员工表查询员工的us
    List<UserSignature> getAllUserSignatures();

    UserSignature saveSignature(String jobNumber, String username, String path) throws IOException;

    String saveFile(MultipartFile file, String jobNumber, String username) throws IOException;
}
