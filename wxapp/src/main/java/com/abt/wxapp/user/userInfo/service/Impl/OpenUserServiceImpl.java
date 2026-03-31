package com.abt.wxapp.user.userInfo.service.Impl;

import com.abt.wxapp.user.userInfo.entity.OpenUserInfo;
import com.abt.wxapp.user.userInfo.model.OpenUserInfoRequestForm;
import com.abt.wxapp.user.userInfo.repository.OpenUserRepository;
import com.abt.wxapp.user.userInfo.service.OpenUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenUserServiceImpl implements OpenUserService {

    private final OpenUserRepository openUserInfoRepository;

    @Override
    public Page<OpenUserInfo> findByQuery(OpenUserInfoRequestForm form) {
        return openUserInfoRepository.findByQuery(
                form.getQuery(),
                form.getChannel(),
                PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Direction.DESC, "createDate"))
        );
    }
}