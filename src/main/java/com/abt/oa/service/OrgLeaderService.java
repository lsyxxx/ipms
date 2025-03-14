package com.abt.oa.service;

import com.abt.oa.entity.OrgLeader;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrgLeaderService {
    void save(List<OrgLeader> list, String jobNumber);

    List<OrgLeader> findAll();

    void deleteByJobNumber(String jobNumber);

    List<OrgLeader> findByJobNumber(String jobNumber);
}
