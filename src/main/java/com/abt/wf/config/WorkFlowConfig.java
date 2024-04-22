package com.abt.wf.config;

import com.abt.common.model.User;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.repository.FlowSettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;

/**
 *
 */
@Component
@Slf4j
public class WorkFlowConfig {
    public static final int IS_DEL = 1;
    public static final int NOT_DEL = 0;

    private final FlowSettingRepository flowSettingRepository;
    private final RepositoryService repositoryService;

    public static final String RBS_TYPE = "rbsType";
    public static final String DEF_KEY_RBS = "rbsMulti";
    public static final String DEF_KEY_TRIP = "rbsTrip";
    public static final String DEF_KEY_PAY_VOUCHER = "rbsPay";
    public static final String DEF_KEY_LOAN = "rbsLoan";
    public static final String DEF_KEY_INV = "rbsInv";
    /**
     * 特殊审批人的流程
     */
    public static final String DEF_KEY_RBS_SPC = "rbsMultiSpecial";

    public static final String SKIP_MANAGER = "rbsFlowSkipManager";
    public static final String DEFAULT_CC = "rbsDefaultCopy";

    private Map<String, ProcessDefinition> processDefinitionMap = new HashMap<>();

    private Map<String, BpmnModelInstance> bpmnModelInstanceMap = new HashMap<>();


    public WorkFlowConfig(FlowSettingRepository flowSettingRepository, RepositoryService repositoryService) {
        this.flowSettingRepository = flowSettingRepository;
        this.repositoryService = repositoryService;
    }

    public static final List<String> financeWorkflowDef = List.of(DEF_KEY_TRIP, DEF_KEY_RBS, DEF_KEY_INV);
    public static final List<String> financeWorkflowBusinessKey = List.of(SERVICE_RBS, SERVICE_INV, SERVICE_TRIP);
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

    /**
     * 默认抄送人
     */
    @Bean
    public List<User> workflowDefaultCopy() {
        log.info("defaultCopy init...");
        final List<FlowSetting> settings = flowSettingRepository.findByTypeOrderByCreateDate(DEFAULT_CC);
        List<User> user = new ArrayList<>();
        settings.forEach(i -> {
            User u = new User();
            u.setId(i.getValue());
            u.setUsername(i.getRemark());
            user.add(u);
        });

        return user;
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

    @Bean("rbsTripBpmnModelInstance")
    @Order(101)
    public BpmnModelInstance rbsTripModelInstance() {
        log.info("rbsTripModelInstance bean init...");
        ProcessDefinition rbsDef = this.processDefinitionMap.get(DEF_KEY_TRIP);

        if (rbsDef == null) {
            rbsDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(DEF_KEY_TRIP).latestVersion().singleResult();
            processDefinitionMap.put(DEF_KEY_TRIP, rbsDef);
        }
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(rbsDef.getId());
        bpmnModelInstanceMap.put(DEF_KEY_TRIP, bpmnModelInstance);
        return bpmnModelInstance;
    }

    @Bean("payVoucherBpmnModelInstance")
    @Order(102)
    public BpmnModelInstance payVoucherModelInstance() {
        log.info("payVoucherModelInstance bean init...");
        ProcessDefinition definition = this.processDefinitionMap.get(DEF_KEY_PAY_VOUCHER);

        if (definition == null) {
            definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(DEF_KEY_PAY_VOUCHER).latestVersion().singleResult();
            processDefinitionMap.put(DEF_KEY_PAY_VOUCHER, definition);
        }
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(definition.getId());
        bpmnModelInstanceMap.put(DEF_KEY_PAY_VOUCHER, bpmnModelInstance);
        return bpmnModelInstance;
    }
    @Bean("loanBpmnModelInstance")
    @Order(103)
    public BpmnModelInstance loanBpmnModelInstance() {
        log.info("loanBpmnModelInstance bean init...");
        ProcessDefinition definition = this.processDefinitionMap.get(DEF_KEY_LOAN);
        if (definition == null) {
            definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(DEF_KEY_LOAN).latestVersion().singleResult();
            processDefinitionMap.put(DEF_KEY_LOAN, definition);
        }
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(definition.getId());
        bpmnModelInstanceMap.put(DEF_KEY_LOAN, bpmnModelInstance);
        return bpmnModelInstance;
    }

    @Bean("invoiceApplyBpmnModelInstance")
    @Order(104)
    public BpmnModelInstance invoiceApplyBpmnModelInstance() {
        log.info("invoiceApplyBpmnModelInstance bean init...");
        ProcessDefinition definition = this.processDefinitionMap.get(DEF_KEY_INV);
        if (definition == null) {
            definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(DEF_KEY_INV).latestVersion().singleResult();
            processDefinitionMap.put(DEF_KEY_INV, definition);
        }
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(definition.getId());
        bpmnModelInstanceMap.put(DEF_KEY_INV, bpmnModelInstance);
        return bpmnModelInstance;
    }

    @Bean("bpmnModelInstanceMap")
    @Order(500)
    public Map<String, BpmnModelInstance> getBpmnModelInstanceMap() {
        log.info("getBpmnModelInstanceMap bean init...");
        return bpmnModelInstanceMap;
    }


}
