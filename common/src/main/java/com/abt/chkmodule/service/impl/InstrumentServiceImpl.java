package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.Instrument;
import com.abt.chkmodule.model.SimpleCheckModule;
import com.abt.chkmodule.repository.CheckModuleInstrumentRelRepository;
import com.abt.chkmodule.repository.CheckModuleRepository;
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
    private final CheckModuleRepository checkModuleRepository;
    public InstrumentServiceImpl(InstrumentRepository instrumentRepository, CheckModuleInstrumentRelRepository checkModuleInstrumentRelRepository, CheckModuleRepository checkModuleRepository) {
        this.checkModuleRepository = checkModuleRepository;
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

    /**
     * 校验设备编号的唯一性
     */
    private void validateInstrumentCode(Instrument instrument) {
        boolean isDuplicate;
        if (StringUtils.hasText(instrument.getId())) {
            isDuplicate = instrumentRepository.existsByCodeAndIdNot(instrument.getCode(), instrument.getId());
        } else {
            isDuplicate = instrumentRepository.existsByCode(instrument.getCode());
        }
        if (isDuplicate) {
            throw new BusinessException("设备编号[" + instrument.getCode() + "]已存在，请重新输入");
        }
    }

    @Override
    public void saveInstrument(Instrument instrument) {
        validateInstrumentCode(instrument);
        instrumentRepository.save(instrument);
    }

    /**
     * 校验并获取设备实体
     * @param id 设备ID
     * @return 设备实体
     */
    private Instrument validateAndGetInstrument(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException("操作失败：设备ID不能为空，请检查参数");
        }

        Instrument instrument = instrumentRepository.findInstrumentById(id);
        if (instrument == null) {
            throw new BusinessException("操作失败：未找到指定的设备 (ID: [" + id + "])，数据可能已被删除或参数错误");
        }
        return instrument;
    }

    @Override
    public Instrument findInstrumentById(String id) {
        return validateAndGetInstrument(id);
    }

    @Override
    public List<SimpleCheckModule> findModulesByInstrumentId(String instrumentId) {
        List<String> moduleIds = checkModuleInstrumentRelRepository.findModuleIdsByInstrumentId(instrumentId);
        return checkModuleRepository.findSimpleModulesByIds(moduleIds);
    }
}
