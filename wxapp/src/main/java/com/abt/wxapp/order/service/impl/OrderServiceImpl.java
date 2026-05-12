package com.abt.wxapp.order.service.impl;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.DynamicScheme;
import com.abt.chkmodule.entity.OptionData;
import com.abt.chkmodule.repository.CheckModuleRepository;
import com.abt.chkmodule.service.DynamicSchemeService;
import com.abt.wxapp.exception.BusinessException;
import com.abt.wxapp.order.entity.WxOrder;
import com.abt.wxapp.order.entity.WxOrderStatus;
import com.abt.wxapp.order.model.OrderCreateResponse;
import com.abt.wxapp.order.model.OrderDetailVo;
import com.abt.wxapp.order.model.OrderFormVo;
import com.abt.wxapp.order.model.OrderListItemVo;
import com.abt.wxapp.order.model.OrderRequestForm;
import com.abt.wxapp.order.repository.WxOrderRepository;
import com.abt.wxapp.order.service.OrderService;
import com.abt.wxapp.security.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 小程序订单
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WxOrderRepository wxOrderRepository;
    private final DynamicSchemeService dynamicSchemeService;
    private final CheckModuleRepository checkModuleRepository;
    private final ObjectMapper objectMapper;

    @Override
    public OrderFormVo findForm(String checkModuleId) {
        CheckModule cm = checkModuleRepository.findById(checkModuleId)
                .orElseThrow(() -> new BusinessException("检测项目不存在"));
        DynamicScheme scheme = dynamicSchemeService.findNewestByCheckModuleId(checkModuleId, cm.getName());
        return new OrderFormVo(
                scheme.getId(),
                cm.getId(),
                cm.getName(),
                cm.getNotice(),
                scheme.getDescription()
        );
    }

    @Override
    @Transactional
    public OrderCreateResponse save(OrderRequestForm form) {
        String userId = SecurityUtil.loginUser().getUserId();
        if (form.getSchemeId() == null || form.getCheckModuleId() == null) {
            throw new BusinessException("缺少表单或检测项目信息");
        }
        DynamicScheme scheme = dynamicSchemeService.findById(form.getSchemeId())
                .orElseThrow(() -> new BusinessException("表单不存在"));
        if (!form.getCheckModuleId().equals(scheme.getCheckModuleId())) {
            throw new BusinessException("检测项目与表单不匹配");
        }
        CheckModule cm = checkModuleRepository.findById(form.getCheckModuleId())
                .orElseThrow(() -> new BusinessException("检测项目不存在"));

        List<OptionData> answers = form.getOptionDataList();
        String json;
        try {
            json = objectMapper.writeValueAsString(answers == null ? List.of() : answers);
        } catch (JsonProcessingException e) {
            throw new BusinessException("表单数据序列化失败", e);
        }

        WxOrder order = new WxOrder();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setCheckModuleId(cm.getId());
        order.setCheckModuleName(cm.getName());
        order.setSchemeId(scheme.getId());
        order.setStatus(WxOrderStatus.PENDING_PAY);
        order.setSampleCount(parseSampleCount(answers));
        order.setOptionJson(json);
        order.setAmount(cm.getPrice());
        wxOrderRepository.save(order);
        return new OrderCreateResponse(order.getId(), order.getOrderNo());
    }

    @Override
    public List<OrderListItemVo> findList(Integer status) {
        String userId = SecurityUtil.loginUser().getUserId();
        List<WxOrder> rows;
        if (status != null) {
            WxOrderStatus st = WxOrderStatus.fromCode(status);
            rows = wxOrderRepository.findByUserIdAndStatusOrderByCreateDateDesc(userId, st);
        } else {
            rows = wxOrderRepository.findByUserIdOrderByCreateDateDesc(userId);
        }
        return rows.stream().map(this::toListVo).toList();
    }

    @Override
    public OrderDetailVo findById(String orderId) {
        String userId = SecurityUtil.loginUser().getUserId();
        WxOrder o = wxOrderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        WxOrderStatus st = o.getStatus();
        return new OrderDetailVo(
                o.getId(),
                o.getOrderNo(),
                o.getCheckModuleName(),
                o.getSampleCount(),
                st.getCode(),
                st.getStatusText(),
                st.getStatusText(),
                formatTime(o.getCreateDate()),
                o.getOptionJson()
        );
    }

    private OrderListItemVo toListVo(WxOrder o) {
        WxOrderStatus st = o.getStatus();
        return new OrderListItemVo(
                o.getId(),
                o.getOrderNo(),
                o.getCheckModuleName(),
                o.getSampleCount(),
                st.getCode(),
                st.getStatusText(),
                st.getStatusText(),
                formatTime(o.getCreateDate())
        );
    }

    private static String formatTime(LocalDateTime t) {
        if (t == null) {
            return null;
        }
        return TS.format(t);
    }

    private static Integer parseSampleCount(List<OptionData> list) {
        if (list == null) {
            return null;
        }
        return list.stream()
                .filter(od -> "sampleCount".equals(od.getCheckComponentId()) && od.getValue() != null && !od.getValue().isBlank())
                .findFirst()
                .map(od -> {
                    try {
                        return Integer.parseInt(od.getValue().trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    private static String generateOrderNo() {
        String day = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        int rnd = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "OD" + day + rnd;
    }
}
