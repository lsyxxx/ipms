package com.abt.finance.repository;

import com.abt.finance.entity.Invoice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    long countById(@NotNull @Size(max = 20) String id);

    List<Invoice> findByRefCode(String refCode);

    @Modifying
    @Transactional
    void deleteByRefCodeAndRefName(String refCode, String refName);
}