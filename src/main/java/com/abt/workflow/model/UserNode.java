package com.abt.workflow.model;

import com.abt.common.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 用户审批节点
 * 设计参考钉钉的流程审批
 */
@Data
@Slf4j
public class UserNode extends BaseNode {

    /**
     * 处理用户
     */
    private User assignee;
    /**
     * 处理时间
     */
    private LocalDateTime executeTime;

    public UserNode() {
        super();
        this.setType(NodeTypeEnum.USER);
    }

    public static UserNode build(String nodeName) {
        UserNode node = new UserNode();
        node.id();
        node.setName(nodeName);
        node.defaultGroup();
        return node;
    }

    /**
     * 生成一个默认id和name的节点
     * id=name=group=uuid
     */
    public static UserNode build() {
        UserNode node = new UserNode();
        node.id();
        node.setName(node.getId());
        node.group(node.getId());
        return node;
    }

    public UserNode assignee(String userId) {
        this.assignee = new User(userId);
        return this;
    }



    @Override
    public void print() {
        log.info("  " + this.info());
    }
}
