package com.abt.workflow.excute;

import com.abt.workflow.Util;
import com.abt.workflow.model.BaseNode;
import com.abt.workflow.model.ProcessBuilder;
import com.abt.workflow.model.Process;
import com.abt.workflow.model.UserNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.alibaba.compileflow.engine.common.util.ClassUtils.newInstance;

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
     * 启动一个流程，返回流程实例
     */
    public Process start(String code) throws Exception {
        Process model = Config.getProcessModel(code);
        log.info("-------------- print model");
        model.printAll();
        Process processInstance = newProcessInstance(code, null);
        processInstance.printAll();

        return processInstance;
    }


    /**
     * 创建流程实例
     * @param code 流程模型code
     * @param vars 流程参数
     */
    public Process newProcessInstance(String code, Map<String, Object> vars) throws Exception {
        Process model = Config.getProcessModel(code);
        Process newInstance = ProcessBuilder.builder().createProcessInstance(model);
        newInstance.addVars(vars);
        return newInstance;
    }

    public BaseNode newBaseNodeInstance(BaseNode model) throws Exception {
        BaseNode newInstance = Util.newInstance(model.getClass());
        newInstance.id();
        newInstance.setType(model.getType());
        newInstance.setName(model.getName());
        newInstance.setGroup(model.getGroup());
        newInstance.setSort(model.getSort());
        return newInstance;
    }




    public static void main(String[] args) throws Exception {
        Executor executor = new Executor();
        Process reimburse = Config.buildReimburse();
        executor.start("commonReimburseLt5000");
    }

}
