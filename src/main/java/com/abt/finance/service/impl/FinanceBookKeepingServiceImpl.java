package com.abt.finance.service.impl;

import com.abt.finance.entity.BankAccount;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.entity.DebitBook;
import com.abt.finance.repository.BankAccountRepository;
import com.abt.finance.repository.CreditBookRepository;
import com.abt.finance.repository.DebitBookRepository;
import com.abt.finance.service.FinanceBookKeepingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资金流入流出
 */
@Service
public class FinanceBookKeepingServiceImpl implements FinanceBookKeepingService {

    private final CreditBookRepository creditBookRepository;
    private final DebitBookRepository debitBookRepository;

    private final BankAccountRepository bankAccountRepository;



    public FinanceBookKeepingServiceImpl(CreditBookRepository creditBookRepository, DebitBookRepository debitBookRepository, BankAccountRepository bankAccountRepository) {
        this.creditBookRepository = creditBookRepository;
        this.debitBookRepository = debitBookRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public CreditBook creditBookKeeping(CreditBook credit) {
        credit.setKeep(true);
        creditBookRepository.save(credit);
        return credit;
    }

    @Override
    public DebitBook debitBookKeeping(DebitBook debit) {
        debit.setKeep(true);
        return debitBookRepository.save(debit);
    }

    @Override
    public List<DebitBook> loadDebits(String businessId) {
        return debitBookRepository.findByBusinessIdOrderByCreateDateDesc(businessId);
    }

    @Override
    public void deleteDebitById(String id) {
        debitBookRepository.deleteById(id);
    }

    @Override
    public List<BankAccount> loadAllBankAccounts() {
        return bankAccountRepository.findAll();
    }


}
