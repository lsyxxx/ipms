package com.abt.wf.service;

import com.abt.wf.service.impl.WorkFlowExecutionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 *
 */
@Slf4j
public class ProcessEndListener implements ExecutionListener {
    private final ReimburseService reimburseService;

    // 但由于监听器可能是由Camunda引擎直接实例化的，而不是由Spring容器管理，直接使用@Autowired可能不会生效。作为解决方案，你可以通过Spring的ApplicationContext来手动获取Bean。
//    private static ApplicationContext applicationContext;

    public ProcessEndListener(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    @Override
    public void notify(DelegateExecution execution) {
        log.info("流程结束 -- processInstanceId: {}", execution.getProcessInstanceId());
        //从ApplicationContext获取bean
//        MyService myService = applicationContext.getBean(MyService.class);
        Object variable = execution.getVariable(WorkFlowExecutionServiceImpl.VARS_ENTITY_ID);
        String entityId = "";
        if (variable != null) {
            entityId = variable.toString();
        }
        reimburseService.finish(entityId);
    }
}
