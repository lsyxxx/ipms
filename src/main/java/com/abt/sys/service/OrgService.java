package com.abt.sys.service;

import com.abt.sys.model.dto.DeptUserList;
import com.abt.sys.model.dto.OrgRequestForm;
import com.abt.sys.model.entity.Org;

import java.util.List;

public interface OrgService {
    /**
     * 查询机构，不分页
     * @param orgRequestForm 参数
     */
    List<Org> getAllBy(OrgRequestForm orgRequestForm);

    /**
     * 查询所有启用的部门和用户
     */
    List<DeptUserList> getAllDeptUserList();
}
