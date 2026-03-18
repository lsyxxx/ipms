package com.abt.wf.service.impl;

import com.abt.wf.entity.TripMain;
import com.abt.wf.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TripServiceImplTest {
    @Autowired
    private TripService tripService;

    @Test
    void load() {
        final TripMain main = tripService.load("202407021719901761542");
        printTrip(main);


    }

    void printTrip(TripMain i) {
        System.out.printf("-编号:%s,出差人员:%s,起止时间:%s-%s,金额:%s。当前审批人id:%s%n",
                i.getId(), i.getStaff(), i.getTripStartDate(), i.getTripEndDate(), i.getSum(), i.getCurrentTask().getAssignee());
        if (i.getDetails() != null) {
            i.getDetails().forEach(d -> {
                System.out.printf("--行程:日期:%s|天数:%d|起讫地址:%s-%s|小计:%s%n",
                        d.getStartDate(), d.getDaySum(), d.getTripOrigin(), d.getTripArrival(), d.getSum());
            });
        }
    }
}