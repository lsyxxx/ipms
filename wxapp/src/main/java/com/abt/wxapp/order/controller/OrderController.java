package com.abt.wxapp.order.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.order.model.OrderCreateResponse;
import com.abt.wxapp.order.model.OrderDetailVo;
import com.abt.wxapp.order.model.OrderFormVo;
import com.abt.wxapp.order.model.OrderListItemVo;
import com.abt.wxapp.order.model.OrderRequestForm;
import com.abt.wxapp.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序下单与订单查询
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/find-form")
    public R<OrderFormVo> findForm(@RequestParam("checkModuleId") @NotBlank(message = "检测项目ID不能为空") String checkModuleId) {
        return R.success(orderService.findForm(checkModuleId));
    }

    @PostMapping("/save")
    public R<OrderCreateResponse> save(@Valid @RequestBody OrderRequestForm form) {
        return R.success(orderService.save(form));
    }

    @GetMapping("/find-list")
    public R<List<OrderListItemVo>> findList(@RequestParam(value = "status", required = false) Integer status) {
        return R.success(orderService.findList(status));
    }

    @GetMapping("/find-by-id/{id}")
    public R<OrderDetailVo> findById(@PathVariable("id") @NotBlank(message = "订单ID不能为空") String id) {
        return R.success(orderService.findById(id));
    }
}
