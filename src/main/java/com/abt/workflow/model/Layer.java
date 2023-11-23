package com.abt.workflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 审批层级
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Deprecated
public class Layer extends BaseNode{
    /**
     * 层级中的节点
     */
    private List<BaseNode> nodes = new ArrayList<>();


    @Override
    public void print() {
        log.info(" " + this.info());
    }
}
