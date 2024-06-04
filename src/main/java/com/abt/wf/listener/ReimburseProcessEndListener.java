package com.abt.wf.listener;

import com.abt.common.model.User;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
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
            System.out.println("==== copy: " + copyStr);
            String[] ids = copyStr.split(",");
            for(String userid : ids) {
                String content = rbs.getCreateUsername() + " 提交的" + rbs.getCost() + "费用报销申请";
//                systemMessageService.sendMessage(NotifyMessage.systemMessage(userid, reimburseService.notifyLink(entityId), msg ));
                //TODO
                String name = "";
                if (userid != null) {
                    final User user = userService.getSimpleUserInfo(userid);
                    name = user.getUsername();
                }
                SystemMessage msg = systemMessageService.createDefaultCopyMessage(userid, name, reimburseService.notifyLink(entityId), content);
                systemMessageService.sendMessage(msg);
                System.out.println("===== SysMsg: " + msg.toString());
            }
        }


        log.info("报销业务流程监听器结束....");
    }
}
