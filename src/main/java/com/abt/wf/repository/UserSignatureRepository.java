package com.abt.wf.repository;

import com.abt.wf.entity.UserSignature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSignatureRepository extends JpaRepository<UserSignature, String> {

    UserSignature findByJobNumber(String jobNumber);
    UserSignature findByUserId(String userId);

}