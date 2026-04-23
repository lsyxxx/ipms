package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.model.StandardItemModuleUnitDTO;
import com.abt.chkmodule.model.StandardStatus;
import com.abt.chkmodule.repository.CheckStandardRepository;
import com.abt.chkmodule.service.CheckStandardService;
import com.abt.sys.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
@Slf4j
@AllArgsConstructor
public class CheckStandardServiceImpl implements CheckStandardService {

    private final CheckStandardRepository checkStandardRepository;


    @Override
    public List<CheckStandard> findByCheckItemIdIn(List<String> checkItemIds) {
        if (!CollectionUtils.isEmpty(checkItemIds)) {
//            return checkStandardRepository.findByCheckItemIds(checkItemIds);
        }

        return new ArrayList<>();
    }

    @Override
    public Page<CheckStandard> findStandardPage(String query,
                                                StandardStatus status,
                                                List<String> levels,
                                                Pageable pageable) {
        boolean hasLevels = levels != null && !levels.isEmpty();
        return checkStandardRepository.findStandardPage(
                query, status, hasLevels, levels, pageable
        );
    }

    @Override
    public List<String> findDistinctLevels() {
        return checkStandardRepository.findDistinctLevels();
    }

    private void validateStandardCode(CheckStandard checkStandard) {
        boolean isDuplicate;
        if (StringUtils.hasText(checkStandard.getId())) {
            isDuplicate = checkStandardRepository.existsByCodeAndIdNot(checkStandard.getCode(), checkStandard.getId());
        } else {
            isDuplicate = checkStandardRepository.existsByCode(checkStandard.getCode());
        }
        if (isDuplicate) {
            throw new BusinessException("标准号[" + checkStandard.getCode() + "]已存在，请重新输入");
        }
    }

    private void validateStandardId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException("标准ID不能为空!");
        }
    }

    @Override
    public CheckStandard findStandardById(String id) {
        validateStandardId(id);
        return checkStandardRepository.findById(id)
                .orElseThrow(() -> new BusinessException("未查询到标准详情(id=" + id + ")"));
    }

    @Override
    public List<StandardItemModuleUnitDTO> findUnitItemsModulesByStandardId(String standardId) {
        validateStandardId(standardId);
        return checkStandardRepository.findUnitItemsModulesByStandardId(standardId);
    }
    @Override
    public void saveStandard(CheckStandard checkStandard) {
        validateStandardCode(checkStandard);
        checkStandardRepository.save(checkStandard);
    }
}
