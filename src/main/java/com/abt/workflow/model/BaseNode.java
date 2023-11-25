package com.abt.workflow.model;

import com.abt.workflow.Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 节点基类
 * 流程所有元素的父类都是此类
 */
@Data
@Slf4j
public abstract class BaseNode implements NodeBehaviour{
    private String id;
    private String name;
    private NodeTypeEnum type = NodeTypeEnum.USER;
    /**
     * 节点顺序
     */
    private int sort = 0;
    /**
     * 分组，自定义。
     * 可以没有
     * 比如：层级1，层级2
     */
    private String group;

    /**
     * 节点连接属性：进来的
     * (in)-> currentNode ->(out)
     */
    private List<String> inRefs = new ArrayList<>();

    /**
     * 节点连接属性：出去
     * (in)-> currentNode ->(out)
     */
    private List<String> outRefs = new ArrayList<>();

    public BaseNode defaultGroup() {
        this.group  = this.id;
        return this;
    }
    public void id() {
        this.id = Util.uuid();
    }

    @Override
    public String info() {
        return "+-- " + type.getName() + ": [" + this.id + ", " + this.name + "]";
    }


    public BaseNode group(String groupName) {
        this.group = groupName;
        return this;
    }

    /**
     * 添加一个inRef节点的Id，添加到InRefs列表最后
     * @param inRefId 进入节点的id
     */
    public BaseNode addInRef(String inRefId) {
        this.inRefs.add(inRefId);
        return this;
    }

    public BaseNode addOutRef(String outRefId) {
        this.outRefs.add(outRefId);
        return this;
    }

}
