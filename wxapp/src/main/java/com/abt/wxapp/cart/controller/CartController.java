package com.abt.wxapp.cart.controller;

import com.abt.wxapp.cart.entity.Cart;
import com.abt.wxapp.cart.service.CartService;
import com.abt.wxapp.security.SecurityUtil;
import com.abt.wxapp.security.WxUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 查询购物车
     */
    @GetMapping("/list")
    public List<Cart> findCartList() {
        final WxUserDetails wxUserDetails = SecurityUtil.loginUser();
        return cartService.findCartList(wxUserDetails.getUserId());
    }



}