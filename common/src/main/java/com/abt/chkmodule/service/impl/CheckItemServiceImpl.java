package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckItemStandardRel;
import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.model.CheckItemSaveDTO;
import com.abt.chkmodule.repository.CheckItemRepository;
import com.abt.chkmodule.repository.CheckItemStandardRelRepository;
import com.abt.chkmodule.repository.CheckStandardRepository;
import com.abt.chkmodule.service.CheckItemService;
import com.abt.chkmodule.service.CheckModuleReference;
import com.abt.sys.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 检测子参数
 */
@Service
public class CheckItemServiceImpl implements CheckItemService {
    private final CheckItemRepository checkItemRepository;

    private final CheckStandardRepository checkStandardRepository;
    private final CheckItemStandardRelRepository checkItemStandardRelRepository;
    public CheckItemServiceImpl(CheckItemRepository checkItemRepository, CheckStandardRepository checkStandardRepository, CheckItemStandardRelRepository checkItemStandardRelRepository ) {
        this.checkItemRepository = checkItemRepository;
        this.checkStandardRepository = checkStandardRepository;
        this.checkItemStandardRelRepository = checkItemStandardRelRepository;
    }


    /**
     * 保存一个子参数
     * @param checkItem 子参数对象
     */
    public void save(CheckItem checkItem) {

    }

    @Override
    public void deleteByCheckModuleId(String checkModuleId) {
        checkItemRepository.deleteByCheckModuleId(checkModuleId);
    }

    @Override
    public boolean existsByCheckModuleId(String checkModuleId) {
        return checkItemRepository.existsByCheckModuleId(checkModuleId);
    }

    @Override
    public String getServiceChineseName() {
        return "检测子参数";
    }

    @Override
    public String getTableName() {
        return "check_item";
    }

    @Override
    public List<CheckItem> findCheckItemsByModuleId(String checkModuleId) {
        List<CheckItem> items = checkItemRepository.findByModuleId(checkModuleId);
        for (CheckItem item : items) {
            List<CheckStandard> standards = checkStandardRepository.findStandardsByItemId(item.getId());
            item.setStandards(standards);
        }
        return items;
    }

    @Override
    @Transactional
    public void updateItemEnabled(String id, boolean enabled) {
        checkItemRepository.updateEnabledStatus(id, enabled);
    }

    @Override
    @Transactional
    public void saveItem(CheckItemSaveDTO dto) {
        CheckItem entityToSave;
        if (StringUtils.hasText(dto.getId())) {
            entityToSave = checkItemRepository.findById(dto.getId())
                    .orElseThrow(() -> new BusinessException("该子参数不存在"));
        } else {
            entityToSave = new CheckItem();
        }
        entityToSave.updateFromDTO(dto);
        entityToSave = checkItemRepository.save(entityToSave);
        checkItemStandardRelRepository.deleteByCheckItemId(entityToSave.getId());
        if (!CollectionUtils.isEmpty(dto.getStandardIds())) {
            List<CheckItemStandardRel> rels = new ArrayList<>();
            for (String stdId : dto.getStandardIds()) {
                rels.add(new CheckItemStandardRel(entityToSave.getId(), stdId));
            }
            checkItemStandardRelRepository.saveAll(rels);
        }
    }
}
