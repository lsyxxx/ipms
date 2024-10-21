package com.abt.wf.listener;

import com.abt.common.model.User;
import com.abt.sys.config.SystemConstants;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.service.ReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 报销业务流程结束
 */
@Component
@Slf4j
public class ReimburseProcessEndListener implements ExecutionListener {
    private final ReimburseService reimburseService;
    private final SystemMessageService systemMessageService;
    private final UserService userService;

    public ReimburseProcessEndListener(ReimburseService reimburseService, SystemMessageService systemMessageService,
                                       @Qualifier("sqlServerUserService") UserService userService) {
        this.reimburseService = reimburseService;
        this.systemMessageService = systemMessageService;
        this.userService = userService;
    }


    @Override
    public void notify(DelegateExecution execution) {
        log.info("报销业务流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            try {
                Reimburse rbs = reimburseService.load(entityId);
                //判断流程状态
                if (Constants.STATE_DETAIL_ACTIVE.equals(rbs.getBusinessState())) {
                    //表示之前一直正常通过，特殊状态在业务中已更改状态
                    rbs.setBusinessState(Constants.STATE_DETAIL_PASS);
                    rbs.setProcessState("COMPLETED");
                }
                rbs.setFinished(true);
                reimburseService.saveEntity(rbs);
                //抄送:

                //资金流出记录，只有流程结束且通过的才记录
                if ("COMPLETED".equals(rbs.getProcessState()) && Constants.STATE_DETAIL_PASS.equals(rbs.getBusinessState())) {
                    reimburseService.writeCreditBook(rbs);
                }


            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
            }

        }
        log.info("报销业务流程监听器结束....");
    }
}
