package com.abt.wf.repository.act;

import com.abt.wf.model.act.ActRuTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActRuTaskRepository extends JpaRepository<ActRuTask, String> {
    ActRuTask findByProcInstId(String procInstId);

}