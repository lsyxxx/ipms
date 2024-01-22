package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, String> {

    long deleteByCompanyId(@Param("companyId") String companyId);
}
