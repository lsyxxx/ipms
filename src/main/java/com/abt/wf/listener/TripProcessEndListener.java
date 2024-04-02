package com.abt.wf.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
public class TripProcessEndListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {

    }
}
