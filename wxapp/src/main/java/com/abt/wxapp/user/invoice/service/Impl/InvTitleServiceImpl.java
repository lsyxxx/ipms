package com.abt.wxapp.user.invoice.service.Impl;

import com.abt.wxapp.user.invoice.entity.OpenUserInvTitle;
import com.abt.wxapp.user.invoice.repository.UserInvTitleRepository;
import com.abt.wxapp.user.invoice.service.InvTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvTitleServiceImpl implements InvTitleService {

    private final UserInvTitleRepository invTitleRepository;

    @Override
    public OpenUserInvTitle saveInvTitle(OpenUserInvTitle invTitle) {
        return invTitleRepository.save(invTitle);
    }

    @Override
    public List<OpenUserInvTitle> findListByUserId(String userId) {
        return invTitleRepository.findByUserIdOrderByCreateDateDesc(userId);
    }

    @Override
    public void deleteInvTitle(String id) {
        invTitleRepository.deleteById(id);
    }

    @Override
    public void setDefaultInvTitle(String userId, String id) {
        invTitleRepository.updateDefaultStatus(userId, id);
    }

    @Override
    public OpenUserInvTitle findTitleById(String id) {
        return invTitleRepository.findTitleById(id);
    }

}