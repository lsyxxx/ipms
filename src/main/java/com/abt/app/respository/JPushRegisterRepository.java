package com.abt.app.respository;

import com.abt.app.entity.JPushRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPushRegisterRepository extends JpaRepository<JPushRegister, String> {
    List<JPushRegister> findByUserid(String userid);
}