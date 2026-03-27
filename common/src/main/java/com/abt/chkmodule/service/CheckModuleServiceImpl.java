package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.Instrument;
import com.abt.chkmodule.repository.CheckModuleRepository;
import com.abt.chkmodule.repository.InstrumentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 检测项目
 */
@Service
@RequiredArgsConstructor
public class CheckModuleServiceImpl implements CheckModuleService {

    private final CheckModuleRepository checkModuleRepository;

    private final InstrumentRepository instrumentRepository;


    /**
     * 查询检测项目的关联仪器
     * @param checkModuleId 检测项目id
     */
    public List<Instrument> findInstrumentsByCheckModuleId(String checkModuleId) {
        if (StringUtils.hasText(checkModuleId)) {
            return new ArrayList<>();
        }
        return instrumentRepository.findByCheckModule(checkModuleId);
    }


    @Override
    public void draft(CheckModule checkModule) {
        checkModuleRepository.save(checkModule);
    }


    /**
     * 发布一个新创建的检测项目
     * 1. 必须满足所有必填条件
     * 2. 必须包含子参数
     */
    public void publishNew(CheckModule checkModule) {

    }

    @Override
    public boolean isDuplicatedName(@NotNull String name) {
        return checkModuleRepository.existsByName(name);
    }

    @Override
    public Optional<CheckModule> findById(String id) {
        return checkModuleRepository.findById(id);
    }



}
