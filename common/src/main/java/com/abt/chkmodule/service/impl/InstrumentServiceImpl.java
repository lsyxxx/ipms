package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.repository.CheckModuleInstrumentRelRepository;
import com.abt.chkmodule.repository.InstrumentRepository;
import com.abt.chkmodule.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepository;

    private final CheckModuleInstrumentRelRepository checkModuleInstrumentRelRepository;

    public InstrumentServiceImpl(InstrumentRepository instrumentRepository, CheckModuleInstrumentRelRepository checkModuleInstrumentRelRepository) {
        this.instrumentRepository = instrumentRepository;
        this.checkModuleInstrumentRelRepository = checkModuleInstrumentRelRepository;
    }

    @Override
    public void deleteByCheckModuleId(String checkModuleId) {
        checkModuleInstrumentRelRepository.deleteByCheckModuleId(checkModuleId);
    }

    @Override
    public boolean existsByCheckModuleId(String checkModuleId) {
        return checkModuleInstrumentRelRepository.existsByCheckModuleId(checkModuleId);
    }

    @Override
    public String getServiceChineseName() {
        return "关联仪器设备";
    }

    @Override
    public String getTableName() {
        return "check_module_instru";
    }
}
