package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceApplyRepository extends JpaRepository<InvoiceApply, String>, JpaSpecificationExecutor<InvoiceApply> {
}
