package com.abt.sys.repository;

import com.abt.sys.model.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TUserRepository extends JpaRepository<TUser, String> {

    TUser findByEmpnum(String empnum);

    List<TUser> findAllByEmpnumIn(Collection<String> empnums);
}