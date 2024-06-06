package com.abt.oa.service;

import com.abt.oa.entity.CarApplyRecord;
import com.abt.oa.model.CarApplyRequestForm;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface CarService {
    List<CarApplyRecord> findBy(CarApplyRequestForm requestForm);

    void writeExcel(List<CarApplyRecord> list) throws IOException;
}
