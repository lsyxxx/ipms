package com.abt.finance.repository;

import com.abt.finance.entity.DebitBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebitBookRepository extends JpaRepository<DebitBook, String> {

    /**
     * 根据业务id查询
     * @param businessId 业务id
     */
    List<DebitBook> findByBusinessIdOrderByCreateDateDesc(String businessId);
}
