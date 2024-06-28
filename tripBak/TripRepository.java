package com.abt.wf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TripRepository extends JpaRepository<TripReimburse, String>, JpaSpecificationExecutor<TripReimburse> {
    List<TripReimburse> findByRootIdOrderBySortAsc(String rootId);
}
