package com.abt.wxapp.order.service;

import com.abt.wxapp.order.model.OrderCreateResponse;
import com.abt.wxapp.order.model.OrderDetailVo;
import com.abt.wxapp.order.model.OrderFormVo;
import com.abt.wxapp.order.model.OrderListItemVo;
import com.abt.wxapp.order.model.OrderRequestForm;

import java.util.List;

public interface OrderService {

    OrderFormVo findForm(String checkModuleId);

    OrderCreateResponse save(OrderRequestForm form);

    List<OrderListItemVo> findList(Integer status);

    OrderDetailVo findById(String orderId);
}
