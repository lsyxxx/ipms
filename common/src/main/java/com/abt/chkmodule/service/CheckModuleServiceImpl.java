package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.*;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 检测项目
 */
@Service
@RequiredArgsConstructor
public class CheckModuleServiceImpl implements CheckModuleService {

    private final CheckModuleRepository checkModuleRepository;
    private final CheckUnitRepository checkUnitRepository;
    private final InstrumentRepository instrumentRepository;
    private final CheckItemRepository checkItemRepository;
    private final CheckItemStandardRelRepository checkItemStandardRelRepository;
    private final CheckModuleInstrumentRelRepository checkModuleInstrumentRelRepository;


    @Override
    public List<CheckUnit> findCheckUnitList(ChannelEnum channelEnum, Boolean enabled) {
        return checkUnitRepository.findList(channelEnum, enabled);
    }

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


    public void saveCheckItemStandardRelList(String checkItemId, List<CheckItemStandardRel> rels) {
        Objects.requireNonNull(checkItemId, "保存子参数关联标准：必须传入子参数id(checkItemId)");
        if (rels == null) {
            return;
        }
        rels.forEach(rel -> {
            rel.setCheckItemId(checkItemId);
        });
        checkItemStandardRelRepository.saveAll(rels);
    }


    @Override
    public void saveCheckItemOne(String checkModuleId, CheckItem checkItem) {
        Objects.requireNonNull(checkModuleId, "必须关联检测项目");
        final CheckItem save = checkItemRepository.save(checkItem);
        saveCheckItemStandardRelList(save.getId(), checkItem.getStdRels());
    }

    @Override
    public void saveCheckItemList(String checkModuleId, List<CheckItem> checkItemList) {
        Objects.requireNonNull(checkModuleId, "保存检测子参数：必须传入检测项目id(checkModuleId)");
        if (checkItemList != null) {
            for (CheckItem item : checkItemList) {
                item.setCheckModuleId(checkModuleId);
                saveCheckItemOne(item.getCheckModuleId(), item);
            }
        }
    }

    public void saveCheckModuleInstrumentList(String checkModuleId, List<CheckModuleInstrumentRel> rels) {
        Objects.requireNonNull(checkModuleId, "保存检测项目关联仪器：必须传入检测项目id(checkModuleId)");
        if (rels != null) {
            rels.forEach(i -> i.setCheckModuleId(checkModuleId));
            checkModuleInstrumentRelRepository.saveAll(rels);
        }
    }

    @Override
    public void deleteCheckItem(String id) {
        checkItemRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void saveCheckModule(CheckModule checkModule) {
        final CheckModule save = checkModuleRepository.save(checkModule);
        saveCheckItemList(save.getId(), checkModule.getCheckItems());
        saveCheckModuleInstrumentList(save.getId(), checkModule.getInstrumentRels());
    }


    @Override
    public boolean isDuplicatedName(@NotNull String name) {
        return checkModuleRepository.existsByName(name);
    }

    @Override
    public Optional<CheckModule> findById(String id) {
        return checkModuleRepository.findById(id);
    }


    @Override
    public void disabledCheckModule(String id) {

    }

    /**
     * 模糊查询检测项目列表，分页数据
     * @param query 模糊查询字段
     * @param name 单字段模糊查询name
     * @return 分页数据
     */
    public Page<CheckModule> findCheckModulesPage(String query, String name, PageRequest pageRequest) {



        return null;

    }


}
