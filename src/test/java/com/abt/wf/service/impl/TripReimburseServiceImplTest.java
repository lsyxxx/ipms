package com.abt.wf.service.impl;

import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;
import com.abt.wf.service.TripReimburseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TripReimburseServiceImplTest {

    @Autowired
    private TripReimburseService tripReimburseService;;

    @Test
    void findMyApplyByCriteriaPageable2() {
    }
}