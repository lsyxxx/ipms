package com.abt.workflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 流程对象
 * 也可以不区分layer/node，全部用node（或者node）作为父类
 */
@Data
@Accessors(chain = true)
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Process extends BaseNode{
    private List<BaseNode> nodes = new ArrayList<>();

    /**
     * 模型id
     */
    private String code;
    /**
     * 模型名称
     */
    private String codeName;
    /**
     * 正在进行的节点id
     */
    private String current = null;

    /**
     * 正在进行的节点下标
     * !!只有串行的时候可以用
     */
    private int currentIndex = 0;

    /**
     * 流程上下文
     * 用于存放流程中的数据等
     */
    private Map<String, Object> context = new HashMap<>();

    /**
     * 流程启动人
     */
    private String starter;

    /**
     * 流程实例启动时间
     */
    private LocalDateTime startTime;

    /**
     * 流程结束时间
     */
    private LocalDateTime endTime;

    public Process() {
        super();
        this.id();
        this.setType(NodeTypeEnum.AUDIT);
    }

    @Override
    public void print() {
        log.info(this.info());
    }

    public Process addNoes(BaseNode ...nodes) {
        this.nodes.addAll(Arrays.stream(nodes).toList());
        return this;
    }

    public Process addVars(Map<String, Object> vars) {
        if (vars == null)
            return this;
        this.context.putAll(vars);
        return this;
    }

    public Object getVar(String key) {
        return this.context.get(key);
    }

    public void printAll() {
        print();
        this.getNodes().forEach(i -> {
            i.print();
        });
    }
}
