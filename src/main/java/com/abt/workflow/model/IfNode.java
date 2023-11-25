package com.abt.workflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 条件选择
 * 只有两种结果true/false
 */
@Slf4j
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class IfNode extends BaseNode implements ExecuteHandler{

    public static final String TRUE_KEY = "true";
    public static final String FALSE_KEY = "false";

    /**
     * 不同条件连接的节点
     * key=条件，IF节点只有true/false两种
     * value=节点ID
     */
    private Map<String, String> refs = new HashMap<>();



    public IfNode() {
        super();
        this.id();
        this.setType(NodeTypeEnum.IF);
        this.defaultGroup();
    }


    @Override
    public void print() {
        log.info("IF选择节点");
    }

    /**
     * 条件结果为true情况下的节点id
     * @param id 连接的节点Id
     */
    public IfNode setTrueRef(String id) {
        refs.put(TRUE_KEY, id);
        return this;
    }

    public Optional<String> getTrueRef() {
        return Optional.ofNullable(refs.get(TRUE_KEY));
    }

    public IfNode setFalseRef(String id) {
        refs.put(FALSE_KEY, id);
        return this;
    }

    public Optional<String> getFalseRef() {
        return Optional.ofNullable(refs.get(FALSE_KEY));
    }



}
