package com.abt.finance.service;

import com.abt.finance.entity.AccountItem;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.model.AccountItemRequestForm;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FinanceCommonService {
    void saveBankAccount(BankAccount bankAccount);

    void delete(String id);

    List<BankAccount> loadAllBankAccounts();

    List<AccountItem> findAllAccountItemsEnabled();

    List<AccountItem> findAccountItemType();

    Page<AccountItem> findAccountItemPageByQuery(AccountItemRequestForm form);

    List<AccountItem> findAccountItemListByQuery(AccountItemRequestForm form);
}
