package com.abt.wfbak.repository;

import com.abt.wfbak.entity.Reimburse;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 *
 */
public interface ReimburseRepository extends JpaRepository<Reimburse, String> {

    Page<Reimburse> findByStarterIdOrderByCreateDateDesc(@Param("starterId") String starterId, Pageable pageable);

    List<Reimburse> findByStarterId(@Param("starterId") String starterId);

    List<Reimburse> findAllByStarterIdOrderByCreateDateDesc(@Param("starterId") String starterId);

}
