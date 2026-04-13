package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.Instrument;
import com.abt.chkmodule.repository.CheckModuleInstrumentRelRepository;
import com.abt.chkmodule.repository.InstrumentRepository;
import com.abt.chkmodule.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.abt.sys.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

    @Override
    public Page<Instrument> findInstrumentPage(String query, List<String> types, String status, List<String> useDepts, Pageable pageable) {
        boolean hasTypes = types != null && !types.isEmpty();
        boolean hasUseDepts = useDepts != null && !useDepts.isEmpty();
        return instrumentRepository.findInstrumentPage(
                query, hasTypes, types,
                status, hasUseDepts, useDepts,
                pageable
        );
    }

    @Override
    public String generateInstrumentCode(String typePrefix, String deptPrefix) {
        String prefix = typePrefix + "-" + deptPrefix + "-";
        String maxCode = instrumentRepository.findMaxCodeByPrefix(prefix);
        int nextSeq = 1;
        if (maxCode != null) {
            nextSeq = Integer.parseInt(maxCode.substring(prefix.length())) + 1;
        }
        return prefix + String.format("%03d", nextSeq);
    }

    @Override
    @Transactional
    public void saveInstrument(Instrument instrument) {
        instrumentRepository.save(instrument);
    }
}
