package com.abt.finance.repository;

import com.abt.finance.entity.Invoice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    long countById(@NotNull @Size(max = 20) String id);

    List<Invoice> findByRefCode(String refCode);

    @Modifying
    @Transactional
    void deleteByRefCodeAndRefName(String refCode, String refName);

    @Modifying
    @Query("""
    update Invoice set isUse = false where id in :ids
""")
    void updateNotUse(List<String> ids);

    @Modifying
    @Query("""
    update Invoice set isUse = false where refCode = :refCode and refName = :refName
""")
    void updateNotUse(String refCode, String refName);

    @Modifying
    @Query("""
    update Invoice set isUse = false where id in :ids
""")
    void updateNotUseByIds(List<String> ids);

    @Query("""
    select i from Invoice i where i.code = :code and i.isUse = true
""")
    Optional<Invoice> findUsedByCode(String code);
}