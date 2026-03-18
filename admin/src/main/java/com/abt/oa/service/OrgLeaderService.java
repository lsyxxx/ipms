package com.abt.oa.service;

import com.abt.common.model.User;
import com.abt.oa.entity.OrgLeader;

import java.util.List;

public interface OrgLeaderService {
    void save(List<OrgLeader> list, String jobNumber);

    void saveOne(OrgLeader orgLeader);

    List<OrgLeader> findAll();

    void deleteByJobNumber(String jobNumber);

    List<OrgLeader> findByJobNumber(String jobNumber);

    void deleteById(String id);

    List<User> findOrgLeaderOptions();
}
