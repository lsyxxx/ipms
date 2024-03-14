package com.abt.finance.service;

import com.abt.finance.entity.CreditBookKeeping;
import com.abt.finance.entity.DebitBookKeeping;

public interface BookKeepingService {

    /**
     * 贷方记账（现金流入）
     */
    void creditBookKeeping(CreditBookKeeping credit);

    /**
     * 借方记账（现金流入）
     */
    void debitBookKeeping(DebitBookKeeping debit);
}
