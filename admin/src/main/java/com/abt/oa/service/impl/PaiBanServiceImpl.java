package com.abt.oa.service.impl;

import com.abt.oa.entity.PaiBan;
import com.abt.oa.reposity.PaiBanRepository;
import com.abt.oa.service.PaiBanService;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class PaiBanServiceImpl implements PaiBanService {

    private final PaiBanRepository paiBanRepository;

    public PaiBanServiceImpl(PaiBanRepository paiBanRepository) {
        this.paiBanRepository = paiBanRepository;
    }


    @Override
    public List<PaiBan> findBetween(LocalDate start, LocalDate end) {
        return WithQueryUtil.build(paiBanRepository.findByPaibandateBetweenOrderByPaibandateAsc(start, end));
    }
}
