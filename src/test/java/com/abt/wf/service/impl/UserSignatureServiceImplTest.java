package com.abt.wf.service.impl;

import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserSignatureServiceImplTest {
    @Autowired
    private UserSignatureRepository userSignatureRepository;


    @Test
    void getAllUserSignatures() {
        final List<UserSignature> list = userSignatureRepository.findAllUserSignatures();
        Assert.notNull(list, "null list");
        for (final UserSignature us : list) {
            if (StringUtils.isBlank(us.getFileName())) {
                System.out.printf("name: %s, jno: %s, fileName: %s%n", us.getUserName(), us.getJobNumber(), us.getFileName());
            }
        }



    }
}