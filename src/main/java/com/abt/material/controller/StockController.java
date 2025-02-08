package com.abt.material.controller;

import com.abt.material.entity.StockOrder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 出入库
 */
@RestController
@Slf4j
@RequestMapping("/stock")
public class StockController {


    /**
     * 入库登记
     */
    public void stockInOrder(StockOrder order) {

    }

}
