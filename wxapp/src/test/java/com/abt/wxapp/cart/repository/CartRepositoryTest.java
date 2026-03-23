package com.abt.wxapp.cart.repository;

import com.abt.wxapp.cart.entity.Cart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    void findByUserId() {
        final List<Cart> list = cartRepository.findByUserId("123");
        Assertions.assertFalse(list.isEmpty());

    }


    @Test
    void save() {
        Cart cart = new Cart();
        cart.setUserId("123");
        cart.setCheckModuleId("CKM_1");

        cartRepository.save(cart);




    }
}