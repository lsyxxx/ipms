package com.abt.wxapp.cart.controller;

import com.abt.wxapp.cart.model.CartListItemVo;
import com.abt.wxapp.cart.service.CartService;
import com.abt.wxapp.common.model.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 查询购物车
     */
    @GetMapping("/find-list")
    public R<List<CartListItemVo>> findList() {
        return R.success(cartService.findList());
    }



}