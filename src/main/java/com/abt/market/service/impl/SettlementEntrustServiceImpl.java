package com.abt.market.service.impl;

import com.abt.market.model.SettlementEntrustDTO;
import com.abt.market.model.SettlementEntrustRequestForm;
import com.abt.market.service.SettlementEntrustService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 项目结算
 */
@Service
public class SettlementEntrustServiceImpl implements SettlementEntrustService {



    /**
     * 查询项目及结算情况，包含所有项目
     * @param requestForm 查询条件（含分页）
     */
    public Page<SettlementEntrustDTO> findSettlementEntrust(SettlementEntrustRequestForm requestForm) {


        return null;
    }
}
