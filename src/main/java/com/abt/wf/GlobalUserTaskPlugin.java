package com.abt.wf;

import com.abt.wf.listener.GlobalUserTaskParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程中所有userTask监听
 */
@Component
public class GlobalUserTaskPlugin extends AbstractProcessEnginePlugin {

    private final GlobalUserTaskParseListener parseListener;

    public GlobalUserTaskPlugin(GlobalUserTaskParseListener parseListener) {
        this.parseListener = parseListener;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();
        if (preParseListeners == null) {
            preParseListeners = new ArrayList<>();
            processEngineConfiguration.setCustomPostBPMNParseListeners(preParseListeners);
        }
        preParseListeners.add(parseListener);
    }

}
