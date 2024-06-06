package com.abt.oa.service.impl;

import com.abt.oa.entity.CarApplyRecord;
import com.abt.oa.model.CarApplyRequestForm;
import com.abt.oa.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarServiceImplTest {

    @Autowired
    private CarService carService;

    @Test
    public void writeExcel() throws IOException {

        CarApplyRequestForm requestForm = new CarApplyRequestForm();
//        requestForm.setStartDate("2024-06-06");
//        requestForm.setEndDate("2024-06-06");
        final List<CarApplyRecord> list = carService.findBy(requestForm);
        System.out.println("findBy-list.size: " +  list.size());
        list.forEach(i -> {
            System.out.println(i.getCarNo());
        });

//        carService.writeExcel(list);
    }

}