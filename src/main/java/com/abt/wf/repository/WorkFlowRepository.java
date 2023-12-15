package com.abt.wf.repository;

import com.abt.wf.model.TaskDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description TODO
 */
public interface WorkFlowRepository{
    List<TaskDTO> findByStartUserid(String userid);

//    @Query(value = "select p.PROC_INST_ID_, p.BUSINESS_KEY_, p.START_USER_ID_, p.START_TIME_, p.END_TIME_, p.PROC_DEF_ID_, p.PROC_DEF_KEY_, p.DELETE_REASON_, p.STATE_, " +
//            " t.TASK_DEF_KEY_, t.NAME_, t.ASSIGNEE_, t.DESCRIPTION_, t.START_TIME_, t.END_TIME_, t.DELETE_REASON_" +
//            " from ACT_HI_PROCINST p left join ACT_HI_TASKINST t on p.PROC_INST_ID_ and t.PROC_INST_ID_" +
//            " where p.START_USER_ID_ = :startUserid " +
//            " and p.BUSINESS_KEY_ not like '%PREVIEW_USER_%'", nativeQuery = true)
//    List<TaskDTO> findByStartUserid(@Param("startUserid") String startUserid);
}
