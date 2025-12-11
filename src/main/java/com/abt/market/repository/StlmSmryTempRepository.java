package com.abt.market.repository;

import com.abt.market.entity.StlmSmryTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StlmSmryTempRepository extends JpaRepository<StlmSmryTemp, String> {
    List<StlmSmryTemp> findByMid(String mid);

    List<StlmSmryTemp> findByMidOrderBySortNo(String mid);
}