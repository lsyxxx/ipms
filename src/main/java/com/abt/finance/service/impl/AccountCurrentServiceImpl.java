package com.abt.finance.service.impl;

import com.abt.finance.model.ReceivableDto;
import com.abt.finance.service.AccountCurrentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 往来账（收付款）
 */
@Service
public class AccountCurrentServiceImpl implements AccountCurrentService {
    @Override
    public void validate(String recType) {

    }

    @Override
    public List<ReceivableDto> getReceivables(String recType) {
        return new ArrayList<>();
    }
}
