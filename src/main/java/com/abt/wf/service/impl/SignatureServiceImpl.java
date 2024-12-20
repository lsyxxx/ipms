package com.abt.wf.service.impl;

import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import com.abt.wf.service.SignatureService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class SignatureServiceImpl implements SignatureService {

    private final UserSignatureRepository userSignatureRepository;

    @Getter
    @Value("${abt.sig.dir}")
    private String sigDir;

    public SignatureServiceImpl(UserSignatureRepository userSignatureRepository) {
        this.userSignatureRepository = userSignatureRepository;
    }


    @Override
    public UserSignature getSignature(String jobNumber) {
        Assert.hasLength(jobNumber, "jobNumber cannot be null or empty!");
        return userSignatureRepository.findByJobNumber(jobNumber);
    }


    @Override
    public String getUserSignatureBase64String(String jobNumber) {
        UserSignature userSignature = getSignature(jobNumber);
        if (userSignature != null) {
            return userSignature.getBase64();
        }
        return "";
    }


    @Override
    public String getUserSignatureBase64StringByUserid(String userid) {
        UserSignature userSignature = getSignatureByUserid(userid);
        if (userSignature != null) {
            return userSignature.getBase64();
        }
        return "";
    }

    @Override
    public UserSignature getSignatureByUserid(String userid) {
        Assert.hasLength(userid, "jobNumber cannot be null or empty!");
        return userSignatureRepository.findByUserId(userid);
    }

    @Override
    public String getSignatureDir() {
        return this.sigDir;
    }
}
