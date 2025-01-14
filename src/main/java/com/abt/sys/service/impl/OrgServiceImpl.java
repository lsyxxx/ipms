package com.abt.sys.service.impl;

import com.abt.oa.OAConstants;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.DeptUserList;
import com.abt.sys.model.dto.OrgRequestForm;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.Org;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.repository.OrgRepository;
import com.abt.sys.service.OrgService;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织机构
 */
@Service
@Slf4j
public class OrgServiceImpl implements OrgService {

    private final OrgRepository orgRepository;
    private final EmployeeRepository employeeRepository;


    public OrgServiceImpl(OrgRepository orgRepository, EmployeeRepository employeeRepository) {
        this.orgRepository = orgRepository;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public List<Org> getAllBy(OrgRequestForm orgRequestForm) {
        Org criteria = new Org();
        criteria.setName(orgRequestForm.getName());
        criteria.setId(orgRequestForm.getId());
        criteria.setCascadeId(orgRequestForm.getCascadeId());
        criteria.setStatus(orgRequestForm.getStatus());
        criteria.setName(orgRequestForm.getQuery());
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues() // 忽略空值
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()) // 部分匹配
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())      // 精确匹配
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("cascadeId", ExampleMatcher.GenericPropertyMatchers.contains())
                ;
        Example<Org> example = Example.of(criteria, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "cascadeId", "sortNo");
        return orgRepository.findAll(example, sort);
    }

    @Override
    public List<DeptUserList> getAllDeptUserList() {
        List<EmployeeInfo> all = employeeRepository.findAllEnabledDeptUsers();
        all = WithQueryUtil.build(all);
        Map<String, List<EmployeeInfo>> deptMap = all.stream()
                .collect(Collectors.groupingBy(EmployeeInfo::getDept, Collectors.toList()));
        Map<String, String> map = all.stream()
                .collect(Collectors
                        .toMap(EmployeeInfo::getDept, EmployeeInfo::getDeptName, (existing, replacement) -> existing));
        List<DeptUserList> list = new ArrayList<>();
        for (Map.Entry<String, List<EmployeeInfo>> entry : deptMap.entrySet()) {
            String deptId = entry.getKey();
            List<EmployeeInfo> userList = entry.getValue();
            DeptUserList dul = new DeptUserList();
            dul.setDeptId(deptId);
            dul.setDeptName(map.get(deptId));
            userList.forEach(u -> dul.addUser(u.getUserid(), u.getName(), u.getJobNumber()));
            list.add(dul);
        }
        return list;
    }

    /**
     * 将org列表组装为tree
     * @param orgs 机构列表
     */
    private void buildOrgTree(List<Org> orgs, Org parent) {
        if (orgs == null) {
            return;
        }
        if (parent == null) {
            return;
        }
        String parentId = parent.getId();
        final List<Org> children = orgs.stream().filter(i -> parentId.equals(i.getParentId())).toList();
        parent.addAllChildren(children);
        for (Org child : parent.getChildren()) {
            buildOrgTree(orgs, child);
        }
    }

    @Override
    public Org getABTOrgTree() {
        final Org abt = orgRepository.findById(OAConstants.ORG_ROOT_ABT).orElseThrow(() -> new BusinessException("未查询到阿伯塔根节点"));
        OrgRequestForm form = new OrgRequestForm();
        form.setStatus(0);
        final List<Org> list = getAllBy(form);
        buildOrgTree(list, abt);
        return abt;
    }
}
