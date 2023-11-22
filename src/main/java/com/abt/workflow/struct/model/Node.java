package com.abt.workflow.struct.model;

import com.abt.common.model.User;
import lombok.Getter;

import java.util.UUID;

/**
 * 节点
 * 设计参考钉钉的流程审批
 */
@Getter
public class Node {
    private String id;
    private String name;

    /**
     * 处理用户
     */
    private User assignee;


    /**
     * 生成一个node
     * @return Node
     */
    public static Node build(String name) {
        Node node = new Node();
        node.id = UUID.randomUUID().toString();
        node.name = name;
        return node;
    }

    /**
     * 指定处理用户
     * @param user
     * @return
     */
    public Node assignee(User user) {
        this.assignee = user;
        return this;
    }

    public void print() {
        System.out.println("    |-- Node: [" + this.id + ", " + this.name + "]");
    }




}
