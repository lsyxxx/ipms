package com.abt.wf.repository;

import com.abt.wf.entity.UserSignature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserSignatureRepository extends JpaRepository<UserSignature, String> {

    UserSignature findByJobNumber(String jobNumber);
    UserSignature findByUserId(String userId);

    List<UserSignature> findByJobNumberIn(Collection<String> jobNumbers);

}