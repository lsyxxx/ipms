package com.abt.finance.controller;

import com.abt.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务模块-报表功能
 */
@RestController
@Slf4j
@RequestMapping("/ac/b")
@Tag(name = "BookController", description = "财务模块-报表功能")
public class BookController {

    /**
     * 下载报表
     * @return
     */
    @Operation(summary = "下载定制报表")
    @Parameter(name = "", description = "定制报表类型")
    @GetMapping("/cus/download")
    public R<String> download() {
        log.info("Download custom book!");
        return R.success("Download a custom selected book!");
    }


    /**
     * 资产负债表
     * @return
     */
    @Operation(summary = "查看资产负债表")
    @Parameter(name = "", description = "资产负债表参数")
    @GetMapping("/bal")
    public R<String> balanceBook() {
        log.info("Balance book page!");
        return R.success("Balance book list!");
    }


    /**
     * 定制报表
     * @param item 单项科目
     * @return
     */
    @Operation(summary = "查看单项报表")
    @Parameter(name = "item", description = "单项报表项目")
    @Parameter(name = "item", description = "单项报表其它查询参数")
    @GetMapping("/item")
    public R<String> customBook(@RequestParam String item) {
        log.info("Select a item book - {}", item);
        return R.success("Balance book list!");
    }
}
