package com.abt.workflow.struct.excute;

import com.abt.workflow.struct.model.Node;
import com.abt.workflow.struct.model.Process;
import com.abt.workflow.struct.model.ProcessBuilder;

import java.util.List;

/**
 * 流程处理器
 */
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
     * 组装结构
     */
    public Process buildReimburse() {
        Node apply = Node.build("reimburseApply");
        Node tech = Node.build("tech");
        Node dept = Node.build("dept");
        Node finMgr = Node.build("finMgr");
        Node ceo = Node.build("ceo");
        Node tax = Node.build("tax");
        Node acc = Node.build("acc");
        //申请节点
        Process process = ProcessBuilder.builder()
                .process("reimburseLt5000")
                .newLayer(apply)
                .newLayer(tech, dept)
                .newLayer(finMgr, ceo, tax)
                .get();
        return process;
    }

    public static void main(String[] args) {
        Executor executor = new Executor();
        Process reimburse = executor.buildReimburse();
        reimburse.print();
    }

}
