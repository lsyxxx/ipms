package com.abt.wf.service.impl;

import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import com.abt.wf.service.UserSignatureService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class UserSignatureServiceImpl implements UserSignatureService {

    private final UserSignatureRepository userSignatureRepository;

    public UserSignatureServiceImpl(UserSignatureRepository userSignatureRepository) {
        this.userSignatureRepository = userSignatureRepository;
    }

    @Override
    public List<UserSignature> getAllUserSignatures() {
        return userSignatureRepository.findAllUserSignatures();
    }

}
