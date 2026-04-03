package com.abt.chkmodule.model;

/**
 * 付款方式（{@link com.abt.chkmodule.entity.CheckComponent}）
 */
public enum PayType {
    /** 按样品数量收费 */
    sample,
    /** 一次性收费 */
    once,
    /** 无需付费 */
    free,
}
