package com.abt.wf.repository;

import com.abt.wf.entity.PayVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PayVoucherRepository extends JpaRepository<PayVoucher, String>, JpaSpecificationExecutor<PayVoucher> {
}
