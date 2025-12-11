package com.abt.testing.service.impl;

import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import com.abt.testing.service.SampleRegistCheckModeuleItemService;
import com.abt.testing.repository.SampleRegistCheckModeuleItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 样品及检测项目
 */
@Service
public class SampleRegistCheckModeuleItemServiceImpl implements SampleRegistCheckModeuleItemService {

    private final SampleRegistCheckModeuleItemRepository sampleRegistCheckModeuleItemRepository;

    public SampleRegistCheckModeuleItemServiceImpl(SampleRegistCheckModeuleItemRepository sampleRegistCheckModeuleItemRepository) {
        this.sampleRegistCheckModeuleItemRepository = sampleRegistCheckModeuleItemRepository;
    }

    @Override
    public List<SampleRegistCheckModeuleItem> findByEntrustIds(List<String> entrustIds, Sort sort) {
        if (sort == null) {
            sort = Sort.by("entrustId", "sampleRegistId", "checkModeuleId");
        }
        return sampleRegistCheckModeuleItemRepository.findAllByEntrustIdIn(entrustIds, sort);
    }
}
