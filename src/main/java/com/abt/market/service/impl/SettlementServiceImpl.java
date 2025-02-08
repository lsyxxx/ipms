package com.abt.market.service.impl;

import com.abt.market.entity.SettlementItem;
import com.abt.market.entity.SettlementMain;
import com.abt.market.repository.SettlementMainRepository;
import com.abt.testing.repository.EntrustRepository;
import com.abt.market.service.SettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 结算
 */
@Service
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final EntrustRepository entrustRepository;
    private final SettlementMainRepository settlementMainRepository;


    public SettlementServiceImpl(EntrustRepository entrustRepository, SettlementMainRepository settlementMainRepository) {
        this.entrustRepository = entrustRepository;
        this.settlementMainRepository = settlementMainRepository;
    }


    @Override
    public void save(SettlementMain main) {
        final List<SettlementItem> settlementItems = main.getSettlementItems();
        if (settlementItems != null &&  !settlementItems.isEmpty()) {
            settlementItems.forEach(i -> {
                i.setMain(main);
            });
        } else {
            main.setSettlementItems(List.of());
        }
        settlementMainRepository.save(main);
    }



}
