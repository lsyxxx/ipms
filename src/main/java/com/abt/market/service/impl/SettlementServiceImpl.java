package com.abt.market.service.impl;

import com.abt.testing.repository.EntrustRepository;
import com.abt.market.service.SettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 结算
 */
@Service
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final EntrustRepository entrustRepository;


    public SettlementServiceImpl(EntrustRepository entrustRepository) {
        this.entrustRepository = entrustRepository;
    }



}
