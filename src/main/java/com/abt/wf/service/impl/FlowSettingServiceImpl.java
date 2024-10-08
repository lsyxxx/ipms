package com.abt.wf.service.impl;

import com.abt.common.util.ValidateUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.repository.FlowSettingRepository;
import com.abt.wf.service.FlowSettingService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
public class FlowSettingServiceImpl implements FlowSettingService {

    private final FlowSettingRepository flowSettingRepository;

    public FlowSettingServiceImpl(FlowSettingRepository flowSettingRepository) {
        this.flowSettingRepository = flowSettingRepository;
    }

    @Override
    public List<FlowSetting> findAll() {
        return flowSettingRepository.findAll(Sort.by(Sort.Direction.DESC, "type", "key"));
    }


    @Override
    public void set(FlowSetting setting) {
        flowSettingRepository.save(setting);
    }

    @Override
    public FlowSetting findById(String id) {
        return flowSettingRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到流程设置" + id));
    }

    @Override
    public void delete(String id) {
        flowSettingRepository.deleteById(id);
    }


    @Transactional
    @Override
    public void deleteAndSaveBatch(List<FlowSetting> list, String type) {
        flowSettingRepository.deleteByType(type);
        flowSettingRepository.saveAllAndFlush(list);
    }

    @Override
    public List<FlowSetting> findByTypeOrderByKeyAsc(String type) {
        ValidateUtil.ensurePropertyNotnull(type, "type(流程设置类型)");
        return flowSettingRepository.findByTypeOrderByKeyAsc(type);
    }
}
