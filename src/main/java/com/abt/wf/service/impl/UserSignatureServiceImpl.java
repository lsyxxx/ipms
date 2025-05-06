package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import com.abt.wf.service.UserSignatureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 *
 */
@Service
public class UserSignatureServiceImpl implements UserSignatureService {

    private final UserSignatureRepository userSignatureRepository;

    @Value("${abt.sig.dir}")
    private String sigDir;

    public UserSignatureServiceImpl(UserSignatureRepository userSignatureRepository) {
        this.userSignatureRepository = userSignatureRepository;
    }

    @Override
    public List<UserSignature> getAllUserSignatures() {
        return userSignatureRepository.findAllUserSignatures();
    }

    private String createFileName(String jobNumber, String username, String company) {
        return company + jobNumber + username + ".png";
    }

    @Override
    public String createFilePath(String fileName) {
        return sigDir + fileName;
    }

    @Override
    public UserSignature saveSignature(String jobNumber, String username, String company) throws IOException {
        if (StringUtils.isBlank(jobNumber)) {
            throw new BusinessException("请输入工号!");
        }

        UserSignature userSignature = UserSignature.create();
        userSignature.setJobNumber(jobNumber);
        userSignature.setUserName(username);
        String fileName = createFileName(jobNumber, username, company);
        userSignature.setFileName(createFileName(jobNumber, username, company));
        Path fp = Paths.get(createFilePath(fileName));
        if (!fp.toFile().exists()) {
            throw new BusinessException("签名文件不存在!请重新上传!");
        }
        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(fp));
        userSignature.setBase64(base64);

        return userSignatureRepository.save(userSignature);
    }

    @Override
    public UserSignature saveSignature(UserSignature us, String fullPath) throws IOException {
        Objects.requireNonNull(us);
        Path fp = Paths.get(fullPath);
        if (!fp.toFile().exists()) {
            throw new BusinessException("签名文件不存在!请重新上传!");
        }
        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(fp));
        us.setBase64(base64);
        return userSignatureRepository.save(us);
    }

    @Override
    public String saveFile(MultipartFile file, String jobNumber, String username, String company) throws IOException {
        String fileName = createFileName(jobNumber, username, company);
        String fullPath = createFilePath(fileName);
        file.transferTo(Paths.get(fullPath));
        return fileName;
    }


    @Override
    public UserSignature findByJobNumber(String jobNumber) {
        return userSignatureRepository.findByJobNumber(jobNumber);
    }

    @Override
    public void deleteUserSignatureByJobNumber(String jobNumber) {
        userSignatureRepository.deleteByJobNumber(jobNumber);
    }


    @Override
    public void saveOrUpdate(UserSignature us) {
        userSignatureRepository.save(us);
    }


}
