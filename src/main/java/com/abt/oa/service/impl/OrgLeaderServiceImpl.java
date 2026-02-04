package com.abt.oa.service.impl;

import com.abt.common.model.User;
import com.abt.oa.entity.OrgLeader;
import com.abt.oa.reposity.OrgLeaderRepository;
import com.abt.oa.service.OrgLeaderService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.abt.salary.model.CheckAuth.SL_CHK_DCEO;
import static com.abt.salary.model.CheckAuth.SL_CHK_DM;

/**
 *
 */
@Service
@Slf4j
public class OrgLeaderServiceImpl implements OrgLeaderService {

    private final OrgLeaderRepository orgLeaderRepository;
    private final EmployeeService employeeService;

    public OrgLeaderServiceImpl(OrgLeaderRepository orgLeaderRepository, EmployeeService employeeService) {
        this.orgLeaderRepository = orgLeaderRepository;
        this.employeeService = employeeService;
    }

    @Transactional
    @Override
    public void save(List<OrgLeader> list, String jobNumber) {
        orgLeaderRepository.deleteByJobNumber(jobNumber);
        orgLeaderRepository.saveAll(list);
    }

    @Override
    public void saveOne(OrgLeader orgLeader) {
        //是否存在重复
        final List<OrgLeader> list = orgLeaderRepository.findByJobNumberAndDeptId(orgLeader.getJobNumber(), orgLeader.getDeptId());
        if (list != null && list.size() > 0) {
            if (isDeptLeader(orgLeader.getRole())) {
                throw new BusinessException(String.format("用户(%s)已设置负责部门:%s。请勿重复设置", orgLeader.getName(), orgLeader.getDeptName()));
            } else {
                throw new BusinessException(String.format("用户(%s)已设置。请勿重复设置", orgLeader.getName()));
            }
        }
        orgLeaderRepository.save(orgLeader);
    }

    private boolean isDeptLeader(String role) {
        return SL_CHK_DCEO.equalsIgnoreCase(role) || SL_CHK_DM.equalsIgnoreCase(role);
    }


    @Override
    public List<OrgLeader> findAll() {
        return orgLeaderRepository.findAll(Sort.by(Sort.Direction.ASC, "jobNumber"));
    }

    @Override
    public void deleteByJobNumber(String jobNumber) {
        orgLeaderRepository.deleteByJobNumber(jobNumber);
    }

    @Override
    public List<OrgLeader> findByJobNumber(String jobNumber) {
        return orgLeaderRepository.findByJobNumber(jobNumber);
    }


    @Transactional
    @Override
    public void deleteById(String id) {
        orgLeaderRepository.deleteById(id);
    }


    @Override
    public List<User> findOrgLeaderOptions() {
        List<User> list = new ArrayList<>();
        final List<EmployeeInfo> ceo = employeeService.findByPosition("总经理");
        list.addAll(emp2User(ceo));
        final List<EmployeeInfo> chief = employeeService.findByPosition("董事长");
        list.addAll(emp2User(chief));
        final List<EmployeeInfo> dceo = employeeService.findByPosition("副总经理");
        list.addAll(emp2User(dceo));
        final List<EmployeeInfo> hr = employeeService.findByPosition("人力资源");
        list.addAll(emp2User(hr));
        return list;
    }

    private List<User> emp2User(List<EmployeeInfo> employeeInfoList) {
        if (employeeInfoList == null || employeeInfoList.isEmpty()) {
            return null;
        }
        List<User> user =  new ArrayList<>();
        for (EmployeeInfo employeeInfo : employeeInfoList) {
            if (!employeeInfo.isExit()) {
                user.add(employeeInfo.toUser());
            }
        }
        return user;
    }
}
