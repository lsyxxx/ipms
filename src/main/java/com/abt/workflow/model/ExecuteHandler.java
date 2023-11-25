package com.abt.workflow.model;


import java.util.Map;

/**
 * 节点的执行方法
 * 每个节点可以实现此接口，自定义节点的执行逻辑
 */
public interface ExecuteHandler {

    /**
     * 处理
     * 暂时传入流程上下文。考虑传入Process（只有最外层，不是整个model结构）
     * @param processInstance 流程实例。
     */
    void execute(Process processInstance);
}
