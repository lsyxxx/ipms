package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.repository.CheckStandardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
            checkStandardRepository.findByCheckItemIds(checkItemIds);
        }

        return new ArrayList<>();
    }

}
