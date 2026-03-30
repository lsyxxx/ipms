package com.abt.wxapp.cart.repository;

import com.abt.wxapp.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {


    @Query("SELECT new com.abt.wxapp.cart.entity.Cart(" +
            "c.id, c.userId, c.checkModuleId, c.checkModuleName, " +
            "c.sampleCount, c.formDataId, c.totalPrice, c.image, " +
            "m.enabled) " +
            "FROM Cart c " +
            "LEFT JOIN CheckModule m ON c.checkModuleId = m.id " +
            "WHERE c.userId = :userId")
    List<Cart> findByUserId(String userId);

}