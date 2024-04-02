package com.abt.finance.service;

import com.abt.finance.entity.BankAccount;

import java.util.List;

public interface FinanceCommonService {
    void saveBankAccount(BankAccount bankAccount);

    void delete(String id);

    List<BankAccount> loadAllBankAccounts();
}
