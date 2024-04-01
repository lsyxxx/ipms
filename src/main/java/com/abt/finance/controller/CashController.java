package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.finance.entity.BankAccount;
import com.abt.finance.entity.DebitBook;
import com.abt.finance.service.FinanceBookKeepingService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资金流入流出
 */
@RestController
@Slf4j
@RequestMapping("/fi/cash")
public class CashController {

    private final FinanceBookKeepingService financeBookKeepingService;

    public CashController(FinanceBookKeepingService financeBookKeepingService) {
        this.financeBookKeepingService = financeBookKeepingService;
    }

    /**
     * 借方记账(资金流出)完成新增/编辑
     */
    @PostMapping("/debit/save")
    public R<DebitBook> debitBookKeeping(@Validated @RequestBody DebitBook debitBookForm) {
        final DebitBook book = financeBookKeepingService.debitBookKeeping(debitBookForm);
        return R.success(book);
    }

    @GetMapping("/debit/load")
    public R<List<DebitBook>> loadDebit(String businessId) {
        final List<DebitBook> debitBooks = financeBookKeepingService.loadDebits(businessId);
        return R.success(debitBooks, debitBooks.size());
    }

    /**
     * 删除一条记账信息
     * @param id 记账id
     */
    @GetMapping("/debit/del")
    public R<Object> deleteDebit(String id) {
        financeBookKeepingService.deleteDebitById(id);
        return R.success("删除成功");
    }

    /**
     * 所有账户信息
     */
    @GetMapping("/bkas")
    public R<List<BankAccount>> loadBankAccounts() {
        final List<BankAccount> bankAccounts = financeBookKeepingService.loadAllBankAccounts();
        return R.success(bankAccounts, bankAccounts.size());
    }
}
