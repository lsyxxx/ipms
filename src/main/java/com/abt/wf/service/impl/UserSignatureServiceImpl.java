package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import com.abt.wf.service.UserSignatureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

/**
 *
 */
@Service
public class UserSignatureServiceImpl implements UserSignatureService {

    private final UserSignatureRepository userSignatureRepository;

    private String sigDir;

    public UserSignatureServiceImpl(UserSignatureRepository userSignatureRepository) {
        this.userSignatureRepository = userSignatureRepository;
    }

    @Override
    public List<UserSignature> getAllUserSignatures() {
        return userSignatureRepository.findAllUserSignatures();
    }




    @Override
    public UserSignature saveSignature(String jobNumber, String username, String path) throws IOException {
        if (StringUtils.isBlank(jobNumber)) {
            throw new BusinessException("请输入工号!");
        }

        UserSignature userSignature = new UserSignature();
        userSignature.setJobNumber(jobNumber);
        userSignature.setUserName(username);
        String fileName = jobNumber + username + ".png";
        userSignature.setFileName(fileName);
        Path fp = Paths.get(path);
        if (!fp.toFile().exists()) {
            throw new BusinessException("签名文件不存在!请重新上传!");
        }
        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(fp));
        userSignature.setBase64(base64);

        return userSignatureRepository.save(userSignature);
    }

    @Override
    public String saveFile(MultipartFile file, String jobNumber, String username) throws IOException {
        String fullPath = sigDir + jobNumber + username + ".png";
        file.transferTo(Paths.get(fullPath));
        return fullPath;
    }

}
