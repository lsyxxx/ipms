package com.abt.wf.repository;

import com.abt.sys.model.SystemFile;
import com.abt.wf.entity.TripDetail;
import com.abt.wf.entity.TripMain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
public class TripMainRepositoryTest {

    @Autowired
    private TripMainRepository tripMainRepository;
    @Autowired
    private TripDetailRepository tripDetailRepository;

    private final Pageable page = PageRequest.of(0, 10, Sort.by("createDate").descending());

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void saveTest() {

        TripMain main = new TripMain();
        main.setReason("测试4444");
        main.setStaff("刘宋菀");
        main.setTripStartDate(LocalDate.now());
        main.setTripEndDate(LocalDate.now());
        main.setDaySum(1);
        main.setSum(new BigDecimal("3333.66"));
        main.setCompany("ABT");
        main.setProcessDefinitionKey("rbsTrip");
//        main.setCreateDate(LocalDateTime.now());
//        main.setUpdateDate(LocalDateTime.now());
        main = tripMainRepository.save(main);

        TripDetail d1 = new TripDetail();
        d1.setMid(main.getId());
        d1.setStartDate(LocalDate.now());
        d1.setTransportation("飞机");
        d1.setTransExpense(new BigDecimal("3333.66"));
        d1.setSum(new BigDecimal("3333.66"));
        main.getDetails().add(d1);



        tripDetailRepository.save(d1);
    }

    @Test
    void find() {
        final TripMain main = tripMainRepository.findWithCurrentTaskById("202407021719901761542");
        printTrip(main);
    }
    @Test
    void find2() {
        final TripMain main = tripMainRepository.findById("202407021719901761542").orElseThrow(() -> new RuntimeException("没找到"));
        printOnlyMain(main);
    }

    @Test
    void findUserApplyByQueryPaged() {
//        final Page<TripMain> page = tripMainRepository.findUserApplyByQueryPaged("", "宇", "", null, null, this.page);
//        assertNotNull(page);
//        System.out.println("total: " + page.getTotalElements());
//        page.getContent().forEach(i -> {
//            System.out.printf("-编号:%s,出差人员:%s,起止时间:%s-%s,金额:%s。当前审批人id:%s%n",
//                    i.getId(), i.getStaff(), i.getTripStartDate(), i.getTripEndDate(), i.getSum(), i.getCurrentTask().getAssignee());
//            if (i.getDetails() != null) {
//                i.getDetails().forEach(d -> {
//                    System.out.printf("--行程:日期:%s|天数:%d|起讫地址:%s-%s|小计:%s%n",
//                            d.getStartDate(), d.getDaySum(), d.getTripOrigin(), d.getTripArrival(), d.getSum());
//                });
//            }
//
//        });
    }

    void printOnlyMain(TripMain i) {
        if (i == null) {
            throw new RuntimeException("trip is null");
        }
        System.out.printf("-编号:%s,出差人员:%s,起止时间:%s-%s,金额:%s。当前审批人id:%s%n",
                i.getId(), i.getStaff(), i.getTripStartDate(), i.getTripEndDate(), i.getSum(), i.getCurrentTask().getAssignee());
    }

    void printTrip(TripMain i) {
        if (i == null) {
            throw new RuntimeException("trip is null");
        }
        System.out.printf("-编号:%s,出差人员:%s,起止时间:%s-%s,金额:%s。当前审批人id:%s%n",
                i.getId(), i.getStaff(), i.getTripStartDate(), i.getTripEndDate(), i.getSum(), i.getCurrentTask().getAssignee());
        if (i.getDetails() != null) {
            i.getDetails().forEach(d -> {
                System.out.printf("--行程%d:日期:%s|天数:%f|起讫地址:%s-%s|小计:%s%n", d.getSort(),
                        d.getStartDate(), d.getDaySum(), d.getTripOrigin(), d.getTripArrival(), d.getSum());
            });
        }
    }

    @Test
    void findWithCurrentTaskById() {
        final TripMain main = tripMainRepository.findWithCurrentTaskById("202407221721613253204");
        printTrip(main);
    }

    void addFile() {
        SystemFile f1 = new SystemFile();
        f1.setService("trip");
        f1.createPath("E:\\upload\\");
        f1.setOriginalName("7月20日呼和浩特一西安支付截图4040.jpg");
        System.out.println(f1);
    }
}