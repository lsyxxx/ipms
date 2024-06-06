package com.abt.oa.reposity;

import com.abt.oa.entity.CarApplyRecord;
import com.abt.oa.model.CarApplyRequestForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarApplyRecordRepositoryTest {

    @Autowired
    private CarApplyRecordRepository carApplyRecordRepository;

    @Test
    void findByKeywordAndDate() {
//        final List<CarApplyRecord> list = carApplyRecordRepository.findByKeywordAndDate("陕U", LocalDateTime.now().minusYears(1), LocalDateTime.now());
        CarApplyRequestForm requestForm = new CarApplyRequestForm();
        requestForm.setStartDate("2024-06-06");
        requestForm.setEndDate("2024-06-06");
        LocalDate s = LocalDate.parse(requestForm.getStartDate());
        LocalDate e = LocalDate.parse(requestForm.getEndDate());
        final List<CarApplyRecord> list = carApplyRecordRepository.findByKeywordAndDate("陕U", s.atStartOfDay(), e.atStartOfDay());
        assertNotNull(list);
        System.out.println("======= size: " + list.size());
        list.forEach(System.out::println);
    }
}