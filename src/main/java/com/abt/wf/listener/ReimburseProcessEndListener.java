package com.abt.wf.listener;

import com.abt.sys.model.entity.NotifyMessage;
import com.abt.sys.service.NotifyMessageService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.service.ReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 报销业务流程结束
 */
@Component
@Slf4j
public class ReimburseProcessEndListener implements ExecutionListener {
    private final ReimburseService reimburseService;
    private final NotifyMessageService notifyMessageService;

    public ReimburseProcessEndListener(ReimburseService reimburseService, NotifyMessageService notifyMessageService) {
        this.reimburseService = reimburseService;
        this.notifyMessageService = notifyMessageService;
    }


    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("报销业务流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
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
            String copyStr = rbs.getCopy();
            List<String> ids = Arrays.asList(copyStr.split(","));
            for(String userid : ids) {
                String msg = rbs.getCreateUsername() + " 提交的" + rbs.getCost() + "费用报销申请";
                notifyMessageService.sendMessage(NotifyMessage.systemMessage(userid, reimburseService.notifyLink(entityId), msg ));
            }
        }


        log.info("报销业务流程监听器结束....");
    }
}
