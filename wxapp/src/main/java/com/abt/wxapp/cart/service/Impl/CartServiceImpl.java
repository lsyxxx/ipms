package com.abt.wxapp.cart.service.Impl;

import com.abt.wxapp.cart.entity.Cart;
import com.abt.wxapp.cart.repository.CartRepository;
import com.abt.wxapp.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public List<Cart> findCartList(String userId) {
        return cartRepository.findByUserId(userId);
    }


}