package com.abt.flow.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 审批信息
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Approval {
    /**
     * 当前处理用户
     */
    private String user;
    private LocalDateTime createTime = LocalDateTime.now();
    /**
     * 结果
     */
    private Object decision;
    /**
     * 评论
     */
    private String comment;

    public Approval(String user, Object decision) {
        this.user = user;
        this.decision = decision;
    }
}
