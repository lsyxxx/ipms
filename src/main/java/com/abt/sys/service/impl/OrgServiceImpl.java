package com.abt.sys.service.impl;

import com.abt.sys.model.dto.DeptUserList;
import com.abt.sys.model.dto.OrgRequestForm;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.Org;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.repository.OrgRepository;
import com.abt.sys.service.OrgService;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.Tree;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
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
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues() // 忽略空值
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()) // 部分匹配
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())      // 精确匹配
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("cascadeId", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Org> example = Example.of(criteria, matcher);
        Sort sort = Sort.by(Sort.Direction.ASC, "cascadeId", "sortNo");
        return orgRepository.findAll(example, sort);
    }


    public List<DeptUserList> getDeptUserList(OrgRequestForm orgRequestForm) {
        List<EmployeeInfo> all = employeeRepository.findAllWithDept();
        all = WithQueryUtil.build(all);



        return null;
    }
}
