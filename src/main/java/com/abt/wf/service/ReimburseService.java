package com.abt.wf.service;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseRequestForm;
import org.springframework.data.domain.Page;

public interface ReimburseService extends WorkFlowService<Reimburse>, BusinessService<ReimburseRequestForm, Reimburse>{
//    Reimburse getRbsCopyEntity(String copyId) throws Exception;
}
