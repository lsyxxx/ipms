package com.abt.wf.repository;

import com.abt.wf.entity.TripDetail;
import com.abt.wf.entity.TripMain;
import jdk.swing.interop.SwingInterOpUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        d1.setMain(main);
        main.getDetails().add(d1);



        tripDetailRepository.save(d1);
    }

    @Test
    void find() {
        final TripMain main = tripMainRepository.findById("202406291719630045126").orElseThrow(() -> new RuntimeException("没找到"));
        assertNotNull(main);
        final List<TripDetail> details = main.getDetails();
        assertNotNull(details);
        System.out.println("details size: " + details);
        details.forEach(d -> {
            System.out.printf("mid:%s,sum:%s", d.getMain().getId(), d.getSum());
        });
    }

    @Test
    void findUserApplyByQueryPaged() {
        final Page<TripMain> page = tripMainRepository.findUserApplyByQueryPaged("", "刘", "", null, null, this.page);
        assertNotNull(page);
        System.out.println("total: " + page.getTotalElements());
        page.getContent().forEach(i -> {
            System.out.printf("-编号:%s,出差人员:%s,起止时间:%s-%s%n",
                    i.getId(), i.getStaff(), i.getTripStartDate(), i.getTripEndDate());
            if (i.getDetails() != null) {
                i.getDetails().forEach(d -> {
                    System.out.printf("--行程:日期:%s|天数:%d|起讫地址:%s-%s|小计:%s%n",
                            d.getStartDate(), d.getDaySum(), d.getTripOrigin(), d.getTripArrival(), d.getSum());
                });
            }

        });
    }
}