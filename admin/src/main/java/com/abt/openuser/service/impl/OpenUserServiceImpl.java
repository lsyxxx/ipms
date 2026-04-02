package com.abt.openuser.service.impl;

import com.abt.openuser.entity.OpenUserInfo;
import com.abt.openuser.model.OpenUserRequestForm;
import com.abt.openuser.repository.OpenUserRepository;
import com.abt.openuser.service.OpenUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 对外用户管理
 */
@Service
@Slf4j
public class OpenUserServiceImpl implements OpenUserService {

    private final OpenUserRepository openUserRepository;

    public OpenUserServiceImpl(OpenUserRepository openUserRepository) {
        this.openUserRepository = openUserRepository;
    }

    @Override
    public Page<OpenUserInfo> findByQuery(OpenUserRequestForm form) {
        return openUserRepository.findByQuery(
                form.getQuery(),
                form.getChannel(),
                PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"))
        );
    }



}
