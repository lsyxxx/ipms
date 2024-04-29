package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceOffset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceOffsetRepository extends JpaRepository<InvoiceOffset, String>, JpaSpecificationExecutor<InvoiceOffset> {
}