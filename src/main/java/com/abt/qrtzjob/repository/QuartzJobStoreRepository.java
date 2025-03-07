package com.abt.qrtzjob.repository;

import com.abt.qrtzjob.entity.QuartzJobStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartzJobStoreRepository extends JpaRepository<QuartzJobStore, String> {
}