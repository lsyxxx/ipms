package com.abt.finance.service.impl;

import com.abt.finance.entity.AccountItem;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.model.AccountItemRequestForm;
import com.abt.finance.repository.AccountItemRepository;
import com.abt.finance.repository.BankAccountRepository;
import com.abt.finance.service.FinanceCommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 财务基础配置
 */
@Service
public class FinanceCommonServiceImpl implements FinanceCommonService {

    private final BankAccountRepository bankAccountRepository;
    private final AccountItemRepository accountItemRepository;



    public FinanceCommonServiceImpl(BankAccountRepository bankAccountRepository, AccountItemRepository accountItemRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountItemRepository = accountItemRepository;
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

    @Override
    public List<AccountItem> findAllAccountItemsEnabled() {
        return accountItemRepository.findByEnabled(true);
    }

    /**
     * 获取科目分类
     */
    @Override
    public List<AccountItem> findAccountItemType() {
        List<AccountItem> byLevel = accountItemRepository.findByLevel(AccountItem.LEVEL_TYPE);
        Collections.sort(byLevel);
        return byLevel;
    }

    @Override
    public Page<AccountItem> findAccountItemPageByQuery(AccountItemRequestForm form) {
        Pageable pageable = PageRequest.of(form.jpaPage(), form.getSize());

        final Page<AccountItem> byQuery = accountItemRepository.findByQuery(form.getQuery(), form.isEnabled(), form.getLevel(), pageable);
        return orderByCode(byQuery);
//        return null;
    }

    @Override
    public List<AccountItem> findAccountItemListByQuery(AccountItemRequestForm form) {
        List<AccountItem> byQuery = accountItemRepository.findByQuery(form.getQuery(), form.isEnabled(), form.getLevel());
        Collections.sort(byQuery);
        return byQuery;
//        return null;
    }

    /**
     * 因为jpa返回的page无法修改数据，对数据排序后重新生成page
     */
    private Page<AccountItem> orderByCode(Page<AccountItem> page) {
        Pageable pageable = page.getPageable();
        List<AccountItem> list = new ArrayList<>(page.getContent());
        Collections.sort(list);
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    /**
     * TODO: 生成级联名称
     */
    public void createAllAccountItemCascade() {
        final List<AccountItem> all = accountItemRepository.findAll();
        for (final AccountItem ai : all) {
            if (ai.getLevel() == 0) {
                continue;
            }
            if (ai.getLevel() == 1) {
                String code = ai.getCode();
                all.stream().filter(i -> i.getLevel() != 1 && i.getCode().startsWith(code)).forEach(i -> i.setCascade(code +"_" + i.getName()));
            }


        }

    }


}
