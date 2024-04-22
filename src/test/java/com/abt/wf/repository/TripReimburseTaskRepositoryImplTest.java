package com.abt.wf.repository;

import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.TripRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.abt.wf.config.WorkFlowConfig.NOT_DEL;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TripReimburseTaskRepositoryImplTest {

    @Autowired
    private TripReimburseTaskRepository repository;

    @Test
    void findDoneMainList() {
        TripRequestForm requestForm = new TripRequestForm();
        requestForm.setPage(1);
        requestForm.setLimit(20);
        requestForm.setUserid("621faa40-f45c-4da8-9a8f-65b0c5353f40");
        requestForm.setUsername("刘宋菀");

        final List<TripReimburse> doneMainList = repository.findDoneMainList(requestForm.getPage(), requestForm.getLimit(), requestForm.getId(), requestForm.getState(),
                requestForm.getUserid(), requestForm.getUsername(), requestForm.getStartDate(), requestForm.getEndDate());
        assertNotNull(doneMainList);
        assertFalse(doneMainList.isEmpty());
        doneMainList.forEach(i -> {
            System.out.println("rootId: " + i.getId());
        });


    }
}