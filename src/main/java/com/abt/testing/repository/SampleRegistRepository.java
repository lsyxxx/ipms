package com.abt.testing.repository;

import com.abt.testing.entity.SampleRegist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRegistRepository extends JpaRepository<SampleRegist, String> {
}