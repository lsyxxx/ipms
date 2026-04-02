package com.abt.openuser.service;

import com.abt.openuser.entity.OpenUserInfo;
import com.abt.openuser.model.OpenUserRequestForm;
import org.springframework.data.domain.Page;

public interface OpenUserService {
    Page<OpenUserInfo> findByQuery(OpenUserRequestForm form);
}
