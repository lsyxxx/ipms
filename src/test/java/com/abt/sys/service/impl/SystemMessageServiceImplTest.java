package com.abt.sys.service.impl;

import com.abt.salary.entity.SalarySlip;
import com.abt.salary.repository.SalarySlipRepository;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.model.entity.TUser;
import com.abt.sys.repository.TUserRepository;
import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.sys.config.SystemConstants.SYSMSG_TYPE_ID_SL_CHK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SystemMessageServiceImplTest {

    @Autowired
    private SystemMessageService systemMessageService;

    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Autowired
    private TUserRepository tUserRepository;

    @Test
    void sendSlipMessage() {
        final List<SalarySlip> slips = salarySlipRepository.findByMainIdIn(List.of("202504171744879901751", "202504171744879890162", "202504171744879865275"));
        Map<String, String> dceoMap = new HashMap<String, String>();
        for (SalarySlip slip : slips) {
            //没发送的不用
            if (!slip.isSend()) {
                continue;
            }
            String username = slip.getName();
            String jno = slip.getJobNumber();
            TUser u = tUserRepository.findByEmpnum(jno);
            if (u != null) {
                //个人工资条
                final SystemMessage msg = systemMessageService.createSystemMessage(u.getId(), username, "/sl/my/index", "您的工资条已发放，请及时查看并确认", "salaryCheck");
                msg.setTypeId(SYSMSG_TYPE_ID_SL_CHK);
                systemMessageService.sendMessage(msg);
            }
            if (StringUtils.isNotBlank(slip.getDceoJobNumber())) {
                dceoMap.put(slip.getDceoJobNumber(), slip.getDceoName());
            }
        }

        //副总通知
        dceoMap.forEach((k, v) -> {
            TUser u = tUserRepository.findByEmpnum(k);
            if (u != null) {
                final SystemMessage msg = systemMessageService.createSystemMessage(u.getId(), v, "/sl/chk/smry/list", "本月工资表已生成，请审核", "salaryCheck");
                msg.setTypeId(SYSMSG_TYPE_ID_SL_CHK);
                systemMessageService.sendMessage(msg);
            }
        });

        //ceo
        final SystemMessage msg = systemMessageService.createSystemMessage("U20230406002", "何爱平", "/sl/chk/smry/list", "本月工资表已生成，请审核", "salaryCheck");
        msg.setTypeId(SYSMSG_TYPE_ID_SL_CHK);
        systemMessageService.sendMessage(msg);
    }

}