package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.repository.CheckItemRepository;
import com.abt.chkmodule.service.CheckItemService;
import com.abt.chkmodule.service.CheckModuleReference;
import org.springframework.stereotype.Service;

/**
 * 检测子参数
 */
@Service
public class CheckItemServiceImpl implements CheckItemService {
    private final CheckItemRepository checkItemRepository;



    public CheckItemServiceImpl(CheckItemRepository checkItemRepository) {
        this.checkItemRepository = checkItemRepository;
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
}
