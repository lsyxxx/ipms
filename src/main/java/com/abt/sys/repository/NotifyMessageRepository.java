package com.abt.sys.repository;

import com.abt.sys.model.entity.NotifyMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyMessageRepository extends JpaRepository<NotifyMessage, String> {

}
