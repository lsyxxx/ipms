package com.abt.finance.service;


import com.abt.finance.entity.BankAccount;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.entity.DebitBook;
import com.abt.finance.model.CashRequestForm;

import java.util.List;

public interface FinanceBookKeepingService {

    /**
     * 借方记账（现金流入）
     */
    CreditBook creditBookKeeping(CreditBook credit);

    /**
     * 贷方记账（现金流出）
     */
    DebitBook debitBookKeeping(DebitBook debit);

    List<DebitBook> loadDebits(String businessId);

    /**
     * 删除一条资金流出记录
     * @param id id
     */
    void deleteDebitById(String id);

    List<BankAccount> loadAllBankAccounts();

    List<CreditBook> loadCreditBookByBusinessId(String businessId);

    void deleteCreditById(String id);

    List<CreditBook> loadCreditByCriteria(CashRequestForm criteria);
}
