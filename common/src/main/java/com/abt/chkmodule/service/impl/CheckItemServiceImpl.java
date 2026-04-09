package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.repository.CheckItemRepository;
import com.abt.chkmodule.repository.CheckStandardRepository;
import com.abt.chkmodule.service.CheckItemService;
import com.abt.chkmodule.service.CheckModuleReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CheckItemServiceImpl(CheckItemRepository checkItemRepository, CheckStandardRepository checkStandardRepository) {
        this.checkItemRepository = checkItemRepository;
        this.checkStandardRepository = checkStandardRepository;
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
        if (items.isEmpty()) {
            return items;
        }

        List<String> itemIds = items.stream().map(CheckItem::getId).toList();
        List<Object[]> stdArrays = checkStandardRepository.findStandardsArrayByItemIds(itemIds);

        Map<String, List<CheckStandard>> stdMap = stdArrays.stream()
                .collect(Collectors.groupingBy(
                        obj -> (String) obj[0],
                        Collectors.mapping(obj -> (CheckStandard) obj[1], Collectors.toList())
                ));
        for (CheckItem item : items) {
            item.setStandards(stdMap.getOrDefault(item.getId(), new ArrayList<>()));
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
    public void saveItem(CheckItem checkItem) {
        checkItemRepository.save(checkItem);
    }
}
