package com.abt.wxapp.checkmodule.service;

import com.abt.sys.exception.BusinessException;
import com.abt.wxapp.checkmodule.entity.DynamicForm;
import com.abt.wxapp.checkmodule.repository.DynamicFormRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 检测项目动态表单
 */
@Service
@Slf4j
public class DynamicFormServiceImpl implements DynamicFormService {

    private final DynamicFormRepository dynamicFormRepository;

    public DynamicFormServiceImpl(DynamicFormRepository dynamicFormRepository) {
        this.dynamicFormRepository = dynamicFormRepository;
    }


    @Override
    public void save(DynamicForm form) {
        // 只能新建不能更新；新行由库分配自增 id，同一检测项目下「最新」用 max(id)
        form.resetId();
        dynamicFormRepository.save(form);
    }


    @Override
    public DynamicForm findNewestByCheckModuleId(String checkModuleId, String checkModuleName) {
        return dynamicFormRepository
                .findFirstByCheckModuleIdOrderByIdDesc(checkModuleId)
                .orElseThrow(() -> new BusinessException("未找到检测项目[" + checkModuleName + "]的微信小程序配置表单"));
    }

    /**
     * 查询所有检测项目的最新表单（同一 cm_id 下 id 最大）
     * @return 每个检测项目一条最新记录
     */
    @Override
    public List<DynamicForm> findAllCheckModuleDynamicForm() {
        //TODO: 1. 分页，2。显示所有检测项目
        return dynamicFormRepository.findAllLatestPerCheckModule();
    }

    @Override
    public void findOneBy(Long id) {
        dynamicFormRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到指定动态表单(id = " + id + ")"));
    }

}
