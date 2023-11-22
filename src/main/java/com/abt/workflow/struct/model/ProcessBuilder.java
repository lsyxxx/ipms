package com.abt.workflow.struct.model;

import java.util.UUID;

/**
 * 流程结构生成器
 */
public class ProcessBuilder {

    private Process process;

    public static ProcessBuilder builder() {
        return new ProcessBuilder();
    }

    public ProcessBuilder process(String processName) {
        this.process = new Process();
        process.setId(UUID.randomUUID().toString())
                .setName(processName);
        return this;
    }

    /**
     * 新增一个层
     * @param nodes 层中的node
     */
    public ProcessBuilder newLayer(Node ...nodes) {
        this.process.add(nodes);
        return this;
    }

    public Process get() {
        return this.process;
    }


}
