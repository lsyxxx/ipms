package com.abt.workflow.excute;

import com.abt.workflow.Util;
import com.abt.workflow.model.ProcessBuilder;
import com.abt.workflow.model.Process;
import com.abt.workflow.model.UserNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 流程处理器
 */
@Component
@Slf4j
public class Executor {
    /**
     * 流程结构
     */
    private Process process;

    /**
     * 处理当前节点
     */
    private void execute() {
        System.out.println("处理当前节点");
    }

    private void next() {
        System.out.println("流转到下一个节点");
    }

    /**
     * 启动一个流程
     */
    public void start(String processId) throws Exception {
        Process model = Config.getProcessModel(processId);
        log.info("-------------- print model");
        model.printAll();
        Process processInstance = Util.newInstance(model.getClass());
        log.info("------------- print new instance");
        processInstance.printAll();

    }


    /**
     * 组装结构
     */


    public static void main(String[] args) throws Exception {
        Executor executor = new Executor();
        Process reimburse = Config.buildReimburse();
        executor.start("commonReimburseLt5000");
    }

}
