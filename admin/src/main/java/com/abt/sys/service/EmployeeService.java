package com.abt.sys.service;

import com.abt.common.model.User;
import com.abt.sys.model.dto.EmployeeInfoRequestForm;
import com.abt.sys.model.dto.UserRequestForm;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.wf.model.EmployeeSignatureDTO;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    EmployeeInfo findByJobNumber(String jobNumber);

    /**
     * 按工号批量查询员工（含部门名），返回 jobNumber -> EmployeeInfo
     * （实现为标量投影，不加载 tUser/userSignature）
     */
    Map<String, EmployeeInfo> findMapWithDeptByJobNumbers(Collection<String> jobNumbers);

    /**
     * 仅查员工部门 Id（T_EmployeeInfo.Dept），不加载关联实体
     */
    String findDeptIdByJobNumber(String jobNumber);

    EmployeeInfo findUserByUserid(String userid);

    User findBasicUserInfoByUserid(String userid);

    /**
     * 查询在职/离职员工
     */
    List<EmployeeInfo> findAllByExit(boolean exit);

    List<EmployeeInfo> getByExample(EmployeeInfo condition);

    Page<User> findUserByQuery(UserRequestForm requestForm);

    /**
     * 部门经理或副总
     */
    List<User> findDCEOs();

    List<EmployeeInfo> findByPosition(String position);

    /**
     * 查询部门经理
     */
    List<EmployeeInfo> findDms();

    List<EmployeeSignatureDTO> findWithSignature(EmployeeInfoRequestForm requestForm);
}
