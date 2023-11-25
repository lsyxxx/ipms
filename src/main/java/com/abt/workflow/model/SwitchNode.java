package com.abt.workflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 条件选择节点
 *
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

    /**
     * 不同条件连接的节点
     * key=条件
     * value=节点ID
     */
    private Map<String, String> refs = new HashMap<>();

    public SwitchNode() {
        super();
        id();
        this.setType(NodeTypeEnum.SWITCH);
    }

    @Override
    public void print() {

    }


}
