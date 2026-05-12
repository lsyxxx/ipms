package com.abt.wxapp.user.invoice.service.impl;

import com.abt.wxapp.exception.BusinessException;
import com.abt.wxapp.user.invoice.entity.OpenUserInvTitle;
import com.abt.wxapp.user.invoice.model.InvTitleRequest;
import com.abt.wxapp.user.invoice.model.InvTitleVo;
import com.abt.wxapp.user.invoice.repository.UserInvTitleRepository;
import com.abt.wxapp.user.invoice.service.InvTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发票抬头业务实现
 */
@Service
@RequiredArgsConstructor
public class InvTitleServiceImpl implements InvTitleService {

    private final UserInvTitleRepository invTitleRepository;

    @Override
    @Transactional
    public InvTitleVo save(InvTitleRequest request) {
        OpenUserInvTitle invTitle = new OpenUserInvTitle();
        invTitle.setId(request.getId());
        invTitle.setUserId(request.getUserId());
        invTitle.setTitleType(request.getTitleType());
        invTitle.setCompanyName(request.getCompanyName());
        invTitle.setTaxNo(request.getTaxNo());
        invTitle.setCompanyAddress(request.getCompanyAddress());
        invTitle.setPhone(request.getPhone());
        invTitle.setBank(request.getBank());
        invTitle.setAccount(request.getAccount());
        if (request.getIsDefault() != null) {
            invTitle.setIsDefault(request.getIsDefault());
        }
        return toVo(invTitleRepository.save(invTitle));
    }

    @Override
    public List<InvTitleVo> findList(String userId) {
        return invTitleRepository.findByUserIdOrderByCreateDateDesc(userId).stream()
                .map(this::toVo)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String id) {
        invTitleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateDefault(String userId, String id) {
        invTitleRepository.updateDefaultStatus(userId, id);
    }

    @Override
    public InvTitleVo findById(String id) {
        OpenUserInvTitle invTitle = invTitleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("发票抬头不存在"));
        return toVo(invTitle);
    }

    private InvTitleVo toVo(OpenUserInvTitle invTitle) {
        return new InvTitleVo(
                invTitle.getId(),
                invTitle.getUserId(),
                invTitle.getTitleType(),
                invTitle.getCompanyName(),
                invTitle.getTaxNo(),
                invTitle.getCompanyAddress(),
                invTitle.getPhone(),
                invTitle.getBank(),
                invTitle.getAccount(),
                invTitle.getIsDefault()
        );
    }
}