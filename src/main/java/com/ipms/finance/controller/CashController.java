package com.ipms.finance.controller;

import com.ipms.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/ac/c/cash")
@Tag(name = "CashController", description = "财务模块-出纳功能-资金模块")
public class CashController {

    /**
     * 资金流出记录
     * @return
     */
    @Operation(summary = "查看资金流出记录")
    @Parameter(name = "params", description = "资金流出记录查询参数")
    @GetMapping("/ob")
    public R<String> cashOutBook() {
        log.info("Cash out book!");
        return R.success("Cash out book!");
    }

    /**
     * 资金流入记录
     * @return
     */
    @Operation(summary = "查看资金流入记录")
    @Parameter(name = "params", description = "资金流入记录查询参数")
    @GetMapping("/ib")
    public R<String> cashInBook() {
        log.info("Cash in book!");
        return R.success("Cash in book!");
    }
}
