package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface FieldWorkRepository extends JpaRepository<FieldWork, String> {

//    @Query("select fw from FieldWork fw ")
//    Page<FieldWork> findTodo(FieldWorkRequestForm form);

    Page<FieldWork> findByCreateUseridAndAttendanceDate(String userid, LocalDate attendanceDate, Pageable pageable);
}