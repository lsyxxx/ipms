package com.abt.wf.service.impl;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.TripReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.SERVICE_TRIP;
import static com.abt.wf.model.TripReimburseForm.KEY_STARTER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TripReimburseServiceImplTest {

    @Autowired
    private TripReimburseService tripReimburseService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    public static final String DEF_KEY = "rbsTrip";
    public static final String DEF_ID = "rbsTrip:2:f9329b39-f49d-11ee-a2ed-a497b12f53fd";

    @Test
    void testApply() {
        TripReimburseForm form = new TripReimburseForm();
        form.setSubmitUserid("abt112");
        form.setSubmitUsername("刘宋菀112");

//        form.setProcessDefinitionId("");
        form.setProcessDefinitionKey(DEF_KEY);

        Map<String, Object> vars = new HashMap<>();
        vars.put(KEY_STARTER, "abt112_starter");

        TripReimburse common = new TripReimburse();
        common.setProcessDefinitionKey(DEF_KEY);
        common.setDeptId("dept1_id1");
        common.setDeptName("技术研发");
        common.setStaff("abt112, abt110, abt113");
        common.setReason("测试创建人名称直接写");
        common.setPayeeId("领款人刘宋菀");
        common.setPayeeName("领款人刘宋菀");
        common.setCompany("ABT");
        common.setCreateUserid("abt112_starter");
        common.setCreateUsername("abt112_startername");
        //跳过必须是null，不能是空字符串
        common.setManagers(null);
        common.setCreateUserid("abt112");
        common.setCreateUsername("刘宋菀");
        form.setCommon(common);

        TripReimburse trip1 = new TripReimburse();
        trip1.setTripStartDate(LocalDate.of(2024, 1, 1));
        trip1.setTripEndDate(LocalDate.of(2024, 1, 5));
        trip1.setTripOrigin("西安");
        trip1.setTripArrival("延安");
        trip1.setTransExpense(new BigDecimal("10000.00"));
        trip1.setTransportation(Constants.TRANSPORTATION_CAR);
        form.getItems().add(trip1);

        TripReimburse trip2 = new TripReimburse();
        trip2.setTripStartDate(LocalDate.of(2024, 2, 2));
        trip2.setTripEndDate(LocalDate.of(2024, 2, 5));
        trip2.setTripOrigin("延安");
        trip2.setTripArrival("北京");
        trip2.setTransExpense(new BigDecimal("2000.00"));
        trip2.setTransportation(Constants.TRANSPORTATION_CAR);
        trip2.setAllowanceDuration(2.00);
        trip2.setAllowanceExpense(new BigDecimal("500.00"));
        trip2.setOtherExpense(new BigDecimal("15000.00"));
        trip2.setOtherExpenseDesc("招待费");
        form.getItems().add(trip2);

        tripReimburseService.apply(form);
    }

    @Test
    void testPreview() {
        TripReimburseForm form = new TripReimburseForm();
//        form.setProcessDefinitionId("rbsTrip:2:f9329b39-f49d-11ee-a2ed-a497b12f53fd");
        form.setProcessDefinitionKey("rbsTrip");
        TripReimburse common = new TripReimburse();
        form.setCommon(common);
        final List<UserTaskDTO> preview = tripReimburseService.preview(form);
        assertNotNull(preview);
        preview.forEach(i -> {
            log.info(i.toString());
        });
    }

    @Test
    void testLoad() {
        String rootId = "202404071712470343590";
        final TripReimburseForm form = tripReimburseService.load(rootId);
        assertNotNull(form);
        System.out.println(form.toString());
    }

    @Test
    void testProcessRecord() {
        final List<FlowOperationLog> logs = tripReimburseService.processRecord("202404071712470343590", SERVICE_TRIP);
        assertNotNull(logs);
        logs.forEach(i -> System.out.println(i.toString()));
    }

    @Test
    void testApprove() {
        TripReimburseForm form = new TripReimburseForm();
        form.setProcessDefinitionKey("rbsTrip");
        form.setRootId("202404071712470343590");
        form.setProcessInstanceId("cc77f9a4-f4a5-11ee-9625-a497b12f53fdd");
        form.setSubmitUsername("刘宋菀");
        form.setSubmitUserid("abt112");
        form.setDecision("reject");
        form.setComment("不同意");

        tripReimburseService.approve(form);
    }


    @Test
    void testFindAllByCriteria() {
        TripRequestForm form = new TripRequestForm();
        form.setLimit(20);
        final List<TripReimburseForm> allByCriteria = tripReimburseService.findAllByCriteriaPageable(form);
        assertNotNull(allByCriteria);
        System.out.println(allByCriteria.size());
        allByCriteria.forEach(i -> System.out.println(i.toString()));
    }


    @Test
    void testFindMyDoneList() {
        TripRequestForm form = new TripRequestForm();
        form.setLimit(20);
        final List<TripReimburseForm> done = tripReimburseService.findMyDoneByCriteriaPageable(form);
        assertNotNull(done);
        System.out.println(done.size());
        done.forEach(i -> System.out.println(i.toString()));
    }

    @Test
    void testMyTodo() {
        TripRequestForm form = new TripRequestForm();
        form.setLimit(20);
        final List<TripReimburseForm> done = tripReimburseService.findMyTodoByCriteria(form);
        assertNotNull(done);
        System.out.println(done.size());
        done.forEach(i -> System.out.println(i.toString()));
    }
}