package com.abt.finance.repository;

import com.abt.finance.entity.CreditBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditBookRepository extends JpaRepository<CreditBook, String> {
}
