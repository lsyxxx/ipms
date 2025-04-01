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
    public static final String DEF_KEY_INVOFFSET = "invOffset";
    public static final String DEF_KEY_PURCHASE = "purchase";
    public static final String DEF_KEY_SUBCONTRACT_TEST = "sbct";

    public static final String SERVICE_RBS = "reimburse";
    public static final String SERVICE_TRIP = "trip";
    public static final String SERVICE_PAY = "pay";
    public static final String SERVICE_LOAN = "loan";
    public static final String SERVICE_INV = "inv";
    public static final String SERVICE_INVOFFSET = "invoffset";

    public static final String SKIP_MANAGER = "rbsFlowSkipManager";
    public static final String DEFAULT_CC = "rbsDefaultCopy";

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

    @Bean("subcontractTestingBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance subcontractTestingBpmnModelInstance() {
        log.info("subcontractTestingBpmnModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_SUBCONTRACT_TEST);
    }

    @Bean("rbsBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance rbsBpmnModelInstance() {
        log.info("rbsBpmnModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_RBS);
    }


    @Bean("rbsTripBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance rbsTripModelInstance() {
        log.info("rbsTripModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_TRIP);
    }

    @Bean("payVoucherBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance payVoucherModelInstance() {
        log.info("payVoucherModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_PAY_VOUCHER);
    }
    @Bean("loanBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance loanBpmnModelInstance() {
        log.info("loanBpmnModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_LOAN);
    }

    @Bean("invoiceApplyBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance invoiceApplyBpmnModelInstance() {
        log.info("invoiceApplyBpmnModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_INV);
    }

    @Bean("invoiceOffsetBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance invoiceOffsetBpmnModelInstance() {
        log.info("invoiceOffsetBpmnModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_INVOFFSET);
    }

    @Bean("purchaseBpmnModelInstance")
    @Order(100)
    public BpmnModelInstance purchaseBpmnModelInstance() {
        log.info("purchaseBpmnModelInstance bean init...");
        return getBpmnModelInstanceFromMap(DEF_KEY_PURCHASE);
    }

    private BpmnModelInstance getBpmnModelInstanceFromMap(String defKey) {
        ProcessDefinition definition = this.processDefinitionMap.get(defKey);
        if (definition == null) {
            definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(defKey).latestVersion().singleResult();
            processDefinitionMap.put(defKey, definition);
        }
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(definition.getId());
        bpmnModelInstanceMap.put(defKey, bpmnModelInstance);
        return bpmnModelInstance;
    }

    @Bean("bpmnModelInstanceMap")
    @Order(5000)
    public Map<String, BpmnModelInstance> getBpmnModelInstanceMap() {
        log.info("getBpmnModelInstanceMap bean init...");
        return bpmnModelInstanceMap;
    }


}
