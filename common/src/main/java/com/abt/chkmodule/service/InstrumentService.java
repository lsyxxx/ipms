package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.Instrument;
import java.util.List;
import java.util.Optional;

import com.abt.chkmodule.model.SimpleCheckModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstrumentService extends CheckModuleReference {

    /**
     * 设备-列表条件查询
     */
    Page<Instrument> findInstrumentPage(String query, List<String> types, String status, List<String> useDepts, Pageable pageable);

    /**
     * 设备-生成设备编号
     */
    String generateInstrumentCode(String type, String useDept);

    /**
     * 设备-保存/编辑
     */
    void saveInstrument(Instrument instrument);

    /**
     * 设备管理-查看详情
     */
    Instrument findInstrumentById(String id);

    /**
     * 设备-查询指定设备关联的检测项目
     */
    List<SimpleCheckModule> findModulesByInstrumentId(String instrumentId);

    void deleteTempInstrument(String id);
}
