package com.abt.workflow.component;

import com.abt.common.model.User;
import com.abt.flow.config.FlowBusinessConfig;
import com.abt.workflow.model.IfNode;
import com.abt.workflow.model.Process;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 判断是否是领导
 */
@Component
public class IfLeaderNode extends IfNode {

    private final FlowBusinessConfig config;

    public IfLeaderNode(FlowBusinessConfig config) {
        this.config = config;
    }

    @Override
    public void execute(Process processInstance) {
        Map<String, User> leaderMap = config.flowSkipManager();
        String starter = processInstance.getStarter();
        if (leaderMap.containsKey(starter)) {

        }
    }
}
