package com.abt.app.respository;

import com.abt.app.entity.PushRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushRegisterRepository extends JpaRepository<PushRegister, String> {
}