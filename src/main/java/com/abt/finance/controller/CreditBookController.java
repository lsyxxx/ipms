package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.model.CreditBookRequestForm;
import com.abt.finance.service.CreditBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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


}
