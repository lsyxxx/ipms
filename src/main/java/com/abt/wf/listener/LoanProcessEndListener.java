package com.abt.wf.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
@AllArgsConstructor
public class LoanProcessEndListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {

    }
}
