package com.abt.wf.repository;

import com.abt.wf.entity.TripReimburse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<TripReimburse, String> {
}
