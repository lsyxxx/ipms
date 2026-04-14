package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.Instrument;
import com.abt.chkmodule.repository.CheckModuleInstrumentRelRepository;
import com.abt.chkmodule.repository.InstrumentRepository;
import com.abt.chkmodule.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

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
        Integer maxSortNo = instrumentRepository.findMaxSortNoByPrefix(prefix);
        int nextSeq = (maxSortNo != null) ? maxSortNo + 1 : 1;
        return prefix + String.format("%03d", nextSeq);
    }

    @Override
    public void saveInstrument(Instrument instrument) {
        boolean isDuplicate;
        if (StringUtils.hasText(instrument.getId())) {
            isDuplicate = instrumentRepository.existsByCodeAndIdNot(instrument.getCode(), instrument.getId());
        } else {
            isDuplicate = instrumentRepository.existsByCode(instrument.getCode());
        }
        if (isDuplicate) {
            throw new BusinessException("设备编号[" + instrument.getCode() + "]已存在，请重新输入");
        }
        instrumentRepository.save(instrument);
    }

    @Override
    public Optional<Instrument> findInstrumentById(String id) {
        return instrumentRepository.findById(id);
    }
}
