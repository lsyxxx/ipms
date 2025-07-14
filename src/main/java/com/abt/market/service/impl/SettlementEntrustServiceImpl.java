package com.abt.market.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.market.entity.SettlementSummary;
import com.abt.market.model.SettlementEntrustDTO;
import com.abt.market.model.SettlementEntrustRequestForm;
import com.abt.market.repository.SettlementMainRepository;
import com.abt.market.repository.SettlementSummaryRepository;
import com.abt.market.service.SettlementEntrustService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目结算
 */
@Service
@Slf4j
public class SettlementEntrustServiceImpl implements SettlementEntrustService {

    private final SettlementMainRepository settlementMainRepository;
    private final SettlementSummaryRepository settlementSummaryRepository;

    public SettlementEntrustServiceImpl(SettlementMainRepository settlementMainRepository, SettlementSummaryRepository settlementSummaryRepository) {
        this.settlementMainRepository = settlementMainRepository;
        this.settlementSummaryRepository = settlementSummaryRepository;
    }


    /**
     * 查询项目及结算情况，包含所有项目
     * 1. 查询所有项目？还是已结算的？
     * 2. 关联金额数据
     * @param requestForm 查询条件（含分页）
     */
    public Page<SettlementEntrustDTO> findSettlementEntrust(SettlementEntrustRequestForm requestForm) {


        return null;
    }

    @Override
    public List<SettlementSummary> checkModuleSettlement(String entrustId) {
        if (StringUtils.isBlank(entrustId)) {
            throw new MissingRequiredParameterException("委托编号(entrustId)");
        }
        return settlementSummaryRepository.entrustSummary(entrustId);
    }
}
