package com.abt.wxapp.order.entity;

import lombok.Getter;

/**
 * 小程序检测订单状态（与前端 mock 数字语义一致，由后端给出文案）
 */
@Getter
public enum WxOrderStatus {
    PENDING_PAY(1, "待支付"),
    TESTING(2, "检测中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String statusText;

    WxOrderStatus(int code, String statusText) {
        this.code = code;
        this.statusText = statusText;
    }

    public static WxOrderStatus fromCode(Integer code) {
        if (code == null) {
            return PENDING_PAY;
        }
        for (WxOrderStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return PENDING_PAY;
    }
}
