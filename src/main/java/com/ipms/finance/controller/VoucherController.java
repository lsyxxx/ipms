package com.ipms.finance.controller;

import com.ipms.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务模块-会计功能-凭证模块
 */
@RestController
@Slf4j
@RequestMapping("/ac/a/vc")
@Tag(name = "VoucherController", description = "财务模块-会计功能")
public class VoucherController {

    /**
     * 查看凭证列表
     * @return
     */
    @Operation(summary = "查看凭证记录")
    @Parameter(name = "params", description = "查询凭证记录参数")
    @GetMapping("/all")
    public R<String> findAll() {
        log.info("Find all voucher....");
        return R.success("All vouchers!");
    }


    /**
     * 修改凭证
     * @return
     */
    @Operation(summary = "修改凭证记录")
    @Parameter(name = "params", description = "修改凭证记录参数")
    @GetMapping("/upd")
    public R<String> update() {
        log.info("Update voucher");
        return R.success("Update a voucher success!");
    }

    /**
     * 删除凭证
     * @return
     */
    @GetMapping("/del")
    @Operation(summary = "删除凭证记录")
    @Parameter(name = "id", description = "被删除的凭证id")
    public R<Long> delete(@RequestParam Long id) {
        log.info("Delete voucher - {}", id);
        return R.success(id);
    }

}
