package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.TripReimburse;
import com.abt.wf.service.TripReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 差旅报销流程结束
 */
@Component
@Slf4j
public class TripProcessEndListener implements ExecutionListener {

    private final TripReimburseService tripReimburseService;

    public TripProcessEndListener(TripReimburseService tripReimburseService) {
        this.tripReimburseService = tripReimburseService;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("差旅报销业务流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            TripReimburse common = tripReimburseService.loadCommonData(entityId);
            //判断流程状态
            if (Constants.STATE_DETAIL_ACTIVE.equals(common.getBusinessState())) {
                //表示之前一直正常通过，特殊状态在业务中已更改状态
                common.setBusinessState(Constants.STATE_DETAIL_PASS);
                common.setProcessState("COMPLETED");
            }
            common.setFinished(true);
            //仅修改common状态
            tripReimburseService.saveEntity(common);
            //抄送

        }
    }
}
