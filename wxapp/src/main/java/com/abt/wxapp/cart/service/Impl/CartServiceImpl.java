package com.abt.wxapp.cart.service.impl;

import com.abt.wxapp.cart.entity.Cart;
import com.abt.wxapp.cart.model.CartListItemVo;
import com.abt.wxapp.cart.repository.CartRepository;
import com.abt.wxapp.cart.service.CartService;
import com.abt.wxapp.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车业务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public List<CartListItemVo> findList() {
        String userId = SecurityUtil.loginUser().getUserId();
        return cartRepository.findByUserId(userId).stream()
                .map(this::toVo)
                .toList();
    }

    private CartListItemVo toVo(Cart cart) {
        return new CartListItemVo(
                cart.getId(),
                cart.getCheckModuleId(),
                cart.getCheckModuleName(),
                cart.getSampleCount(),
                cart.getFormDataId(),
                cart.getTotalPrice(),
                cart.getImage(),
                cart.isActive()
        );
    }
}