package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 *
 */
public interface ReimburseRepository extends JpaRepository<Reimburse, String> {

    Page<Reimburse> findByStarterIdOrdOrderByCreateDateDesc(@Param("starterId") String starterId, Pageable pageable);

}
