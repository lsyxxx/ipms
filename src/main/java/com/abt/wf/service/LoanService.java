package com.abt.wf.service;

import com.abt.wf.entity.Loan;
import com.abt.wf.model.LoanRequestForm;

public interface LoanService extends WorkFlowService<Loan>, BusinessService<LoanRequestForm, Loan>{

}
