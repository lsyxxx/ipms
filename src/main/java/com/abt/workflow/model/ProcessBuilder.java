package com.abt.workflow.model;

import com.abt.workflow.Util;

import java.util.Arrays;

/**
 * 流程结构生成器
 */
public class ProcessBuilder {

    private Process process;

    public static ProcessBuilder builder() {
        return new ProcessBuilder();
    }

    public ProcessBuilder process(String processName) {
        this.process = newProcess();
        this.process.setName(processName);
        return this;
    }

    public ProcessBuilder process(Process process) {
        this.process = process;
        return this;
    }

    /**
     * 添加一组用户节点到最后
     * @param groupName 分组名称
     * @param names 节点名称
     */
    public ProcessBuilder addUserNodeGroup(String groupName, String ...names) {
        for (int i = 0; i < names.length; i++) {
            this.process.addNoes(UserNode.build(names[i]).group(groupName));
        }
        return this;
    }

    private Process newProcess() {
        if (this.process == null) {
            this.process = new Process();
        }
        return this.process;
    }

    /**
     * 添加一个节点在最后
     * @param nodes 添加的节点
     */
    public ProcessBuilder addNodes(BaseNode ...nodes) {
        newProcess();
        this.process.getNodes().addAll(Arrays.stream(nodes).toList());
        return this;
    }


    public Process get() {
        //process默认名称
        if (this.process.getName() == null) {
            this.process.setName("Process:" + Util.uuid());
        }
        return this.process;
    }

    public ProcessBuilder id(String processId) {
        this.process.setId(processId);
        return this;
    }

    /**
     * 默认审批流
     * 默认添加一个申请节点
     */
    public static ProcessBuilder defaultAuditProcess(String processName) {
        ProcessBuilder builder = ProcessBuilder.builder();
        builder.process(processName);
        builder.addUserNodeGroup("apply", "apply");
        return builder;
    }


}
