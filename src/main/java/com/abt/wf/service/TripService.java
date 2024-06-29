package com.abt.wf.service;

import com.abt.wf.entity.TripMain;
import com.abt.wf.model.TripRequestForm;
import org.springframework.data.domain.Page;

public interface TripService extends WorkFlowService<TripMain>, BusinessService<TripRequestForm, TripMain>{

    TripMain getEntityWithCurrentTask(String id);

}
