package com.abt.sys.repository;

import com.abt.sys.model.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TUserRepository extends JpaRepository<TUser, String> {

    TUser findByEmpnum(String empnum);
}