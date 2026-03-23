package com.abt.wxapp.cart.service;

import com.abt.wxapp.cart.entity.Cart;

import java.util.List;

/**
 * 购物车业务接口
 */
public interface CartService {

    /**
     * 根据用户ID获取清单列表
     */
    List<Cart> findCartList(String userid);


}