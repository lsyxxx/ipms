package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.finance.entity.AccountItem;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.service.FinanceCommonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基础配置的
 */
@RestController
@Slf4j
@RequestMapping("/fi")
public class CommonController {
    private final FinanceCommonService financeCommonService;

    public CommonController(FinanceCommonService financeCommonService) {
        this.financeCommonService = financeCommonService;
    }

    @PostMapping("/bankAcc/save")
    public R<Object> saveBankAccount(@Validated @RequestBody BankAccount bankAccount) {
        financeCommonService.saveBankAccount(bankAccount);
        return R.success("添加成功");
    }

    @GetMapping("/bankAcc/del")
    public R<Object> delete(String id) {
        financeCommonService.delete(id);
        return R.success("删除成功");
    }

    @GetMapping("/bankAcc/all")
    public R<List<BankAccount>> loadAllBankAccounts() {
        final List<BankAccount> bankAccounts = financeCommonService.loadAllBankAccounts();
        return R.success(bankAccounts, bankAccounts.size());
    }

    @GetMapping("/accItem/all")
    public R<List<AccountItem>> findAllAccountItems() {
        final List<AccountItem> allAccountItems = financeCommonService.findAllAccountItemsEnabled();
        return R.success(allAccountItems, allAccountItems.size());
    }
}
