package com.abt.wfbak.config;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.repository.FlowSettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Component
@Slf4j
public class WorkFlowConfig {

    private final FlowSettingRepository flowSettingRepository;
    private final RepositoryService repositoryService;

    public static final String RBS_TYPE = "rbsType";
    public static final String DEF_KEY_RBS = "rbsMulti";

    public static final String SKIP_MANAGER = "rbsFlowSkipManager";

    private Map<String, ProcessDefinition> processDefinitionMap = new HashMap<>();

    private Map<String, BpmnModelInstance> bpmnModelInstanceMap = new HashMap<>();


    public WorkFlowConfig(FlowSettingRepository flowSettingRepository, RepositoryService repositoryService) {
        this.flowSettingRepository = flowSettingRepository;
        this.repositoryService = repositoryService;
    }

    /**
     * 查询报销类型
     */
    @Bean
    public List<FlowSetting> queryReimburseType() {
        return flowSettingRepository.findByTypeOrderByCreateDate(RBS_TYPE);
    }

    @Bean
    public List<FlowSetting> queryFlowManagerList() {
        return flowSettingRepository.findByTypeOrderByCreateDate(SKIP_MANAGER);
    }

    @Bean("rbsMultiProcessDefinition")
    @Order(1)
    public ProcessDefinition getRbsMultiProcessDefinition() {
        ProcessDefinition rbsDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(DEF_KEY_RBS).latestVersion().singleResult();
        processDefinitionMap.put(DEF_KEY_RBS, rbsDef);
        return rbsDef;
    }

    @Bean("processDefinitionMap")
    @Order(50)
    public Map<String, ProcessDefinition> processDefinitionMap() {
        log.info("processDefinitionMap bean init...");
        return this.processDefinitionMap;
    }

    @Bean("rbsBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance rbsBpmnModelInstance() {
        log.info("rbsBpmnModelInstance bean init...");
        ProcessDefinition rbsDef = this.processDefinitionMap.get(DEF_KEY_RBS);
        if (rbsDef == null) {
            rbsDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(DEF_KEY_RBS).latestVersion().singleResult();
            processDefinitionMap.put(DEF_KEY_RBS, rbsDef);
        }
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(rbsDef.getId());
        bpmnModelInstanceMap.put(DEF_KEY_RBS, bpmnModelInstance);
        return bpmnModelInstance;
    }

    @Bean("bpmnModelInstanceMap")
    @Order(150)
    public Map<String, BpmnModelInstance> getBpmnModelInstanceMap() {
        log.info("getBpmnModelInstanceMap bean init...");
        return bpmnModelInstanceMap;
    }




}
