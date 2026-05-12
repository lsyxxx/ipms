package com.abt.wxapp.order.repository;

import com.abt.wxapp.order.entity.WxOrder;
import com.abt.wxapp.order.entity.WxOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WxOrderRepository extends JpaRepository<WxOrder, String> {

    List<WxOrder> findByUserIdOrderByCreateDateDesc(String userId);

    List<WxOrder> findByUserIdAndStatusOrderByCreateDateDesc(String userId, WxOrderStatus status);

    Optional<WxOrder> findByIdAndUserId(String id, String userId);
}
