package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.model.StandardItemModuleUnitDTO;
import com.abt.chkmodule.model.StandardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CheckStandardService {
    /**
     * 查询指定子参数的标准，如果checkItemIds传入空，那么返回空
     * @param checkItemIds 检测子参数
     */
    List<CheckStandard> findByCheckItemIdIn(List<String> checkItemIds);

    /**
     * 标准-列表条件查询
     */
    Page<CheckStandard> findStandardPage(String query,
                                         StandardStatus status,
                                         List<String> levels,
                                         Pageable pageable);
    /**
     * 标准-查询所有不同的标准等级
     */
    List<String> findDistinctLevels();

    /**
     * 标准管理-保存/编辑
     */
    void saveStandard(CheckStandard checkStandard);

    /**
     * 标准库-查询指定标准详情
     */
    CheckStandard findStandardById(String id);

    /**
     * 查询指定标准关联的检测项目及子参数
     */
    List<StandardItemModuleUnitDTO> findUnitItemsModulesByStandardId(String standardId);

}
