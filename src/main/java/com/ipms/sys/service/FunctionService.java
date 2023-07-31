package com.ipms.sys.service;

import com.ipms.sys.mapper.FunctionMapper;
import com.ipms.sys.model.entity.Function;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限控制
 */
@Service
public class FunctionService implements CrudService<Function, Long> {

    private FunctionMapper functionMapper;

    public FunctionService(FunctionMapper functionMapper) {
        this.functionMapper = functionMapper;
    }

    @Override
    public List<Function> findAll() {
        return functionMapper.findVisible();
    }

    /**
     * 删除，重置删除标志为1
     * @param function
     */
    @Override
    public void delete(Function function) {
        functionMapper.softDelete(function.getId());
    }

    @Override
    public Function findById(Long id) {
        return functionMapper.findById(id);
    }

    @Override
    public Long insert(Function function) {
        return functionMapper.insert(function);
    }

    /**
     * 更新基本信息
     * @param function
     */
    @Override
    public void update(Function function) {

    }

    public Function enabled(Function function) {
        function.setEnabled(true);
        functionMapper.updateEnabled(function);
        return function;
    }

    public Function disabled(Function function) {
        function.setEnabled(false);
        functionMapper.updateEnabled(function);
        return function;
    }
}
