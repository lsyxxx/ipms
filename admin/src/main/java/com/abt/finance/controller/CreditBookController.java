package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.model.CreditBookRequestForm;
import com.abt.finance.service.CreditBookService;
import com.abt.finance.service.ICreditBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 资金流出
 */
@RestController
@Slf4j
@RequestMapping("/fi/cash/credit")
public class CreditBookController {

    private final CreditBookService creditBookService;

    public CreditBookController(CreditBookService creditBookService) {
        this.creditBookService = creditBookService;
    }

    @PostMapping("/find")
    public R<List<CreditBook>> findBy(@RequestBody CreditBookRequestForm form) {
        final Page<CreditBook> page = creditBookService.findBySpecification(form);
        return R.success(page.getContent(), (int)page.getTotalElements(), "查询成功");
    }

    @GetMapping("/load/biz")
    public R<Object> loadBusiness(@RequestParam String type, @RequestParam String bizId) {
        final ICreditBook business = creditBookService.loadBusiness(type, bizId);
        return R.success(business);
    }

    @Secured("JS_FI_CREDIT_DEL")
    @GetMapping("/del")
    public R<Object> delete(@RequestParam String id) {
        creditBookService.delete(id);
        return R.success("删除成功");
    }

}
