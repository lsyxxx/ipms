package com.abt.finance.repository;

import com.abt.finance.entity.Invoice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    long countById(@NotNull @Size(max = 20) String id);
}