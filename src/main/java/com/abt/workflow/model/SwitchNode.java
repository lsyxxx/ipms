package com.abt.workflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 条件选择节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class SwitchNode extends BaseNode{
    /**
     * 条件
     */
    private String condition;


    public SwitchNode() {
        super();
        id();
        this.setType(NodeTypeEnum.SWITCH);
    }

    @Override
    public void print() {

    }
}
