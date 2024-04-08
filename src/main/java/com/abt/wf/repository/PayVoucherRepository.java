package com.abt.wf.repository;

import com.abt.wf.entity.PayVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayVoucherRepository extends JpaRepository<PayVoucher, String>, JpaSpecificationExecutor<PayVoucher> {

}
