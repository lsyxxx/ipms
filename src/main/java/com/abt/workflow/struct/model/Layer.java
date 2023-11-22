package com.abt.workflow.struct.model;

import com.abt.workflow.struct.Util;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 审批层级
 */
@Getter
public class Layer {
    private String id;
    private String name;
    /**
     * 层级中的节点
     */
    private List<Node> nodes = new ArrayList<>();

    /**
     * 层中添加一个节点
     * @param node 节点
     * @return layer 本层
     */
    public Layer add(Node node) {
        this.nodes.add(node);
        return this;
    }

    /**
     * 在层中添加节点
     * @param nodes
     * @return
     */
    public Layer add(Node ...nodes) {
        this.nodes.addAll(Arrays.stream(nodes).toList());
        return this;
    }

    /**
     * 自动生成层名称
     */
    public static Layer build() {
        return Layer.build(Util.uuid());
    }
    public static Layer build(String name)  {
        Layer layer = new Layer();
        layer.id = UUID.randomUUID().toString();
        layer.name = name;
        return layer;
    }

    public void print() {
        System.out.println("  |-- Layer: [" + this.id + ", " + this.name + "]");
        this.nodes.forEach(i -> {
            i.print();
        });
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

}
