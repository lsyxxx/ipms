package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.repository.CheckItemRepository;
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
}
