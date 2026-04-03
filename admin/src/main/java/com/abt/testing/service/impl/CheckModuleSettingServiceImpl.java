package com.abt.testing.service.impl;

import com.abt.chkmodule.repository.CheckModuleRepository;
import com.abt.chkmodule.service.CheckModuleReference;
import com.abt.sys.exception.BusinessException;
import com.abt.testing.service.CheckModuleSettingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测项目配置
 */
@Service
@Slf4j
public class CheckModuleSettingServiceImpl implements CheckModuleSettingService {

    private final CheckModuleRepository checkModuleRepository;

    private final List<? extends CheckModuleReference> serviceList;



    public CheckModuleSettingServiceImpl(CheckModuleRepository checkModuleRepository, List<? extends CheckModuleReference> serviceList) {
        this.checkModuleRepository = checkModuleRepository;
        this.serviceList = serviceList;
    }


    /**
     * 删除checkModule前校验
     *
     * @param id 检测项目id
     * @return List<String> 仍存在关联的业务信息(非表名)
     */
    public List<String> checkModuleDeleteValidate(String id) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        List<String> err = new ArrayList<>();
        //1. 不能关联其他表
        if (serviceList != null) {
            for (CheckModuleReference checkModuleReference : serviceList) {
                final boolean exists = checkModuleReference.existsByCheckModuleId(id);
                if (exists) {
                    err.add(checkModuleReference.getServiceChineseName() + "(" + checkModuleReference.getTableName() + ")");
                }
            }
        }
        return err;

    }


    /**
     * 仅删除checkModule
     * @param id 检测项目id
     */
    private void deleteCheckModuleRecordById(String id) {
        checkModuleRepository.deleteById(id);
    }


    @Transactional
    @Override
    public void deleteCheckModuleById(String id) {
        if (!StringUtils.hasText(id)) {
            log.warn("deleteCheckModuleById: 未传入检测项目id");
            return;
        }
        final List<String> err = checkModuleDeleteValidate(id);
        if (!CollectionUtils.isEmpty(err)) {
            String errTxt = String.join("; ", err);
            throw new BusinessException(errTxt);
        }
        deleteCheckModuleRecordById(id);
    }
}
