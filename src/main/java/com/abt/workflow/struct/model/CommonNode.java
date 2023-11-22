package com.abt.workflow.struct.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 普通节点
 */
public class CommonNode extends Node{
    /**
     * 当前节点处理人
     */
    private String executor;
    private LocalDateTime executeTime;
}
