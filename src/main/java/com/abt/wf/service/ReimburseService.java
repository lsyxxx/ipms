package com.abt.wf.service;

import com.abt.finance.service.ICashCreditService;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.WithInvoice;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface ReimburseService extends WorkFlowService<Reimburse>, BusinessService<ReimburseRequestForm, Reimburse>, ICashCreditService<Reimburse> {
}
