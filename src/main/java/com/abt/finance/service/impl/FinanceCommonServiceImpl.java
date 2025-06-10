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
        return bankAccountRepository.findAll(Sort.by(Sort.Order.asc("company"), Sort.Order.desc("account")));
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
    }

    @Override
    public List<AccountItem> findAccountItemListByQuery(AccountItemRequestForm form) {
        List<AccountItem> byQuery = accountItemRepository.findByQuery(form.getQuery(), form.isEnabled(), form.getLevel());
        Collections.sort(byQuery);
        return byQuery;
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
     * 生成级联名称
     */
    @Override
    public void createAllAccountItemCascade() {
        final List<AccountItem> all = accountItemRepository.findAll();
        final List<AccountItem> lv1List = all.stream().filter(i -> i.getLevel() == 1).toList();
        final List<AccountItem> lv2List = all.stream().filter(i -> i.getLevel() == 2).toList();
        final List<AccountItem> lv3List = all.stream().filter(i -> i.getLevel() == 3).toList();
        lv1List.forEach(i -> {
            i.setCascadeName(i.getName());
            i.setCascadeCode(i.getCode());
        });
        for (AccountItem ai : lv1List) {
            String c1 = ai.getCode();
            String n1 = ai.getName();
            final List<AccountItem> found = lv2List.stream().filter(i -> i.getCode().startsWith(c1)).toList();
            found.forEach(i -> {
                i.setCascadeCode(c1 + "_" + i.getCode());
                i.setCascadeName(n1 + "_" + i.getName());
            });
        }

        for (AccountItem ai : lv2List) {
            lv3List.stream().filter(i -> i.getCode().startsWith(ai.getCode())).forEach(i -> {
                i.setCascadeName(ai.getCascadeName() + "_" + ai.getName());
                i.setCascadeCode(ai.getCascadeCode() + "_" + ai.getCode());
            });
        }
        accountItemRepository.saveAll(all);
        final long count = all.stream().filter(i -> StringUtils.isBlank(i.getCascadeName())).count();
        System.out.println("count: " + count);
        all.stream().filter(i -> StringUtils.isBlank(i.getCascadeCode())).forEach(i -> {
            i.setCascadeCode(i.getCode());
            i.setCascadeName(i.getName());
        });
        accountItemRepository.saveAll(all);

    }


}
