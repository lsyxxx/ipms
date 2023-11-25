package com.abt.workflow.excute;

import com.abt.sys.exception.BusinessException;
import com.abt.workflow.model.IfNode;
import com.abt.workflow.model.Process;
import com.abt.workflow.model.ProcessBuilder;
import com.abt.workflow.model.UserNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程配置
 */

//@Configuration
public class Config {

    private static final Map<String, Process> processMap = new ConcurrentHashMap<>();

    static {
        init();
    }

    /**
     * TODO: 其它方式将模型读入内存
     */
    public static void init() {
        processMap.put("commonReimburseLt5000", buildReimburse());
    }

//    @Bean("techUserNode")
    public UserNode techNode() {
        return UserNode.build("tech");
    }

    /**
     * 普通员工少于5000报销
     */
//    @Bean("commonReimburseLt5000")
    public static Process buildReimburse() {
        UserNode tech = UserNode.build("tech");
        UserNode dept = UserNode.build("dept");
        UserNode finMgr = UserNode.build("finMgr");
        UserNode ceo = UserNode.build("ceo");
        UserNode tax = UserNode.build("tax");
        UserNode acc = UserNode.build("acc");
        Process process = ProcessBuilder.defaultAuditProcess("commonReimburseLt5000")
                .id("commonReimburseLt5000")
                .addUserNodeGroup("layer1", "tech", "dept")
                .addUserNodeGroup("layer2", "finMgr", "ceo", "tax")
                .get()
                ;
        return process;
    }


    public static Process buildReimburseWithSwitch() {
        UserNode tech = UserNode.build("tech");
        UserNode dept = UserNode.build("dept");
        UserNode finMgr = UserNode.build("finMgr");
        UserNode ceo = UserNode.build("ceo");
        UserNode tax = UserNode.build("tax");
        UserNode acc = UserNode.build("acc");
        Process process = ProcessBuilder.defaultAuditProcess("commonReimburseLt5000")
                .id("commonReimburseLt5000")
//                .addNodes(ifLeader)
                .addUserNodeGroup("layer1", "tech", "dept")
                .addUserNodeGroup("layer2", "finMgr", "ceo", "tax")
                .get()
                ;
        return process;
    }

    public static Map<String, Process> getProcessMap() {
        return Config.processMap;
    }


    /**
     * 从内存中获取流程模型
     * @param code
     * @return
     */
    public static Process getProcessModel(String code) {
        Process process = Config.processMap.get(code);
        if (process != null) {
            return process;
        }
        throw new BusinessException("未找到流程模型, code = " + code);
    }

}
