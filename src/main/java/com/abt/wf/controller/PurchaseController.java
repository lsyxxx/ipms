package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.service.PurchaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 采购流程
 */
@RestController
@Slf4j
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }


    @PostMapping("/apply")
    public R<Object> apply(PurchaseApplyMain form) {
        purchaseService.apply(form);
        return R.success("提交采购申请成功");
    }
}
