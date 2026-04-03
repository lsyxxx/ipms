package com.abt.chkmodule.model;

import lombok.Getter;

/**
 * 标准状态（ordinal 与持久化一致：0/1/2）
 * <ul>
 *   <li>0 — 废止</li>
 *   <li>1 — 现行</li>
 *   <li>2 — 即将实施</li>
 * </ul>
 */
@Getter
public enum StandardStatus {

    /** 废止 */
    REPEALED("废止"),
    /** 现行 */
    CURRENT("现行"),
    /** 即将实施 */
    PENDING_EFFECTIVE("即将实施");

    private final String label;

    StandardStatus(String label) {
        this.label = label;
    }
}
