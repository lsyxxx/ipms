package com.abt.finance.service.impl;

import com.abt.finance.entity.BankAccount;
import com.abt.finance.repository.BankAccountRepository;
import com.abt.finance.service.FinanceCommonService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 财务基础配置
 */
@Service
public class FinanceCommonServiceImpl implements FinanceCommonService {

    private final BankAccountRepository bankAccountRepository;

    public FinanceCommonServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void saveBankAccount(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void delete(String id) {
        bankAccountRepository.deleteById(id);
    }

    @Override
    public List<BankAccount> loadAllBankAccounts() {
        return bankAccountRepository.findAll(Sort.by(Sort.Direction.DESC, "account"));
    }

}
