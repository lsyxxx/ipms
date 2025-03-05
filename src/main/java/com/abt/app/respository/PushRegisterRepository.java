package com.abt.app.respository;

import com.abt.app.entity.PushRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushRegisterRepository extends JpaRepository<PushRegister, String> {
    List<PushRegister> findByUserid(String userid);
}