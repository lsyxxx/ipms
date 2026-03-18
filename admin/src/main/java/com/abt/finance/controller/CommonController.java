package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.finance.entity.AccountItem;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.model.AccountItemRequestForm;
import com.abt.finance.service.FinanceCommonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/accItem/query")
    public R<List<AccountItem>> findAllAccountItemList(@ModelAttribute AccountItemRequestForm form) {
        List<AccountItem> list = new ArrayList<>();
        int total = 0;
        if (form.noPaging()) {
            list = financeCommonService.findAccountItemListByQuery(form);
        } else {
            Page<AccountItem> page = financeCommonService.findAccountItemPageByQuery(form);
            list = page.getContent();
            total = (int) page.getTotalElements();
        }
        return R.success(list, total);
    }
}
