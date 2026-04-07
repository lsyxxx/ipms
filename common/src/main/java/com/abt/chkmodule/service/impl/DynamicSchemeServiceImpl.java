package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.DynamicScheme;
import com.abt.chkmodule.repository.DynamicSchemeRepository;
import com.abt.chkmodule.service.DynamicSchemeService;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 检测项目动态表单
 */
@Service
@Slf4j
public class DynamicSchemeServiceImpl implements DynamicSchemeService {

    private final DynamicSchemeRepository dynamicSchemeRepository;

    public DynamicSchemeServiceImpl(DynamicSchemeRepository dynamicSchemeRepository) {
        this.dynamicSchemeRepository = dynamicSchemeRepository;
    }


    @Override
    public void save(DynamicScheme form) {
        // 只能新建不能更新；新行由库分配自增 id，同一检测项目下最新的用 max(id)
        form.resetId();
        //1. scheme本身
        dynamicSchemeRepository.save(form);
    }


    @Override
    public DynamicScheme findNewestByCheckModuleId(String checkModuleId, String checkModuleName) {
        return dynamicSchemeRepository
                .findFirstByCheckModuleIdOrderByIdDesc(checkModuleId)
                .orElseThrow(() -> new BusinessException("未找到检测项目[" + checkModuleName + "]的微信小程序配置表单"));
    }

    /**
     * 查询所有检测项目的最新表单（同一 cm_id 下 id 最大）
     *
     * @return 每个检测项目一条最新记录
     */
    @Override
    public List<DynamicScheme> findAllCheckModuleDynamicForm() {
        //TODO: 1. 分页，2。显示所有检测项目
        return dynamicSchemeRepository.findAllLatestPerCheckModule();
    }

    @Override
    public void findOneBy(Long id) {
        dynamicSchemeRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到指定动态表单(id = " + id + ")"));
    }


    /**
     * scheme保存前校验
     */
    public void saveValidate(DynamicScheme scheme) {
        //1. checkComponents必须有
        if (CollectionUtils.isEmpty(scheme.getComponents())) {
            throw new BusinessException("未添加任何组件。必须添加组件");
        }

        //2. 每个组件校验

    }


    /**
     * 单组件校验
     */
    private void checkComponentValidate() {

    }


    /**
     * 正式发布前校验
     */
    public void publishValidate() {

    }
}
