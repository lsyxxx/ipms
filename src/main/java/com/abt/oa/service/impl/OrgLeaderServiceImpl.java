package com.abt.oa.service.impl;

import com.abt.oa.entity.OrgLeader;
import com.abt.oa.reposity.OrgLeaderRepository;
import com.abt.oa.service.OrgLeaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class OrgLeaderServiceImpl implements OrgLeaderService {

    private final OrgLeaderRepository orgLeaderRepository;

    public OrgLeaderServiceImpl(OrgLeaderRepository orgLeaderRepository) {
        this.orgLeaderRepository = orgLeaderRepository;
    }

    @Transactional
    @Override
    public void save(List<OrgLeader> list, String jobNumber) {
        orgLeaderRepository.deleteByJobNumber(jobNumber);
        orgLeaderRepository.saveAll(list);
    }

    @Override
    public List<OrgLeader> findAll() {
        return orgLeaderRepository.findAll(Sort.by(Sort.Direction.ASC, "jobNumber"));
    }

    @Override
    public void deleteByJobNumber(String jobNumber) {
        orgLeaderRepository.deleteByJobNumber(jobNumber);
    }

    @Override
    public List<OrgLeader> findByJobNumber(String jobNumber) {
        return orgLeaderRepository.findByJobNumber(jobNumber);
    }
}
