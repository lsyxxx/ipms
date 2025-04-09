package com.abt.wf.service;

import com.abt.wf.entity.UserSignature;

import java.util.List;

public interface UserSignatureService {
    //根据员工表查询员工的us
    List<UserSignature> getAllUserSignatures();
}
