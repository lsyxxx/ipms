package com.abt.sys.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.User;
import com.abt.common.util.QueryUtil;
import com.abt.sys.model.dto.EmployeeInfoRequestForm;
import com.abt.sys.model.dto.UserRequestForm;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.Org;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.model.EmployeeSignatureDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;


    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EntityManager entityManager) {
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
    }


    @Override
    public EmployeeInfo findByJobNumber(String jobNumber) {
        if (StringUtils.isBlank(jobNumber)) {
            throw new MissingRequiredParameterException("员工工号");
        }
        return WithQueryUtil.build(employeeRepository.findByJobNumber(jobNumber));
    }


    @Override
    public EmployeeInfo findUserByUserid(String userid) {
        if (StringUtils.isBlank(userid)) {
            throw new MissingRequiredParameterException("员工id");
        }
        return WithQueryUtil.build(employeeRepository.findOneByUserid(userid));
    }


    @Override
    public User findBasicUserInfoByUserid(String userid) {
        EmployeeInfo employeeInfo = findUserByUserid(userid);
        User user = new User();
        user.setId(employeeInfo.getUserid());
        user.setEmployeeId(employeeInfo.getId());
        user.setUsername(employeeInfo.getName());
        user.setDeptId(employeeInfo.getDept());
        user.setDeptName(employeeInfo.getDeptName());
        user.setCode(employeeInfo.getJobNumber());
        return user;
    }


    @Override
    public List<EmployeeInfo> findAllByExit(boolean exit) {
        return WithQueryUtil.build(employeeRepository.findByIsExit(exit));
    }

    @Override
    public List<EmployeeInfo> getByExample(EmployeeInfo condition) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains)
                .withMatcher("position", ExampleMatcher.GenericPropertyMatcher::contains)
                .withIgnorePaths("enable", "sort");
        Example<EmployeeInfo> example = Example.of(condition, matcher);
        return WithQueryUtil.build(employeeRepository.findAll(example, Sort.by("jobNumber").ascending()));
    }

    @Override
    public Page<User> findUserByQuery(UserRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getSize());
        final Page<EmployeeInfo> page = employeeRepository
                .findByQuery(requestForm.getQuery(), requestForm.isEnabled(), requestForm.getStatus(), requestForm.getDeptId(), pageable);
        WithQueryUtil.build(page.getContent());
        List<User> ul = new ArrayList<>();
        for (EmployeeInfo one : page.getContent()) {
            User u = new User();
            u.setId(one.getUserid());
            u.setUsername(one.getName());
            u.setCode(one.getJobNumber());
            u.setDeptId(one.getDept());
            u.setDeptName(one.getDeptName());
            u.setPosition(one.getPosition());
            ul.add(u);
        }
        return new PageImpl<>(ul, pageable, page.getTotalElements());
    }


    @Override
    public List<User> findDCEOs() {
        final List<EmployeeInfo> list = employeeRepository.findDCEOs();
        List<User> ul = new ArrayList<>();
        list.forEach(i -> {
            ul.add(i.toUser());
        });
        return ul;
    }

    @Override
    public List<EmployeeInfo> findDms() {
        return employeeRepository.findDms();
    }


    @Override
    public List<EmployeeSignatureDTO> findWithSignature(EmployeeInfoRequestForm requestForm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeSignatureDTO> selectQuery = cb.createQuery(EmployeeSignatureDTO.class);
        Root<EmployeeInfo> root = selectQuery.from(EmployeeInfo.class);
        final Join<EmployeeInfo, UserSignature> sigJoin = root.join("userSignature", JoinType.LEFT);
        final Join<EmployeeInfo, Org> orgJoin = root.join("department", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(requestForm.getJobNumber())) {
            predicates.add(cb.like(root.get("jobNumber"), QueryUtil.like(requestForm.getJobNumber())));
        }
        if (StringUtils.isNotBlank(requestForm.getUsername())) {
            predicates.add(cb.like(root.get("name"), QueryUtil.like(requestForm.getUsername())));
        }
        if (requestForm.getIsExit() != null) {
            predicates.add(cb.equal(root.get("isExit"), requestForm.getIsExit()));
        }
        if (StringUtils.isNotBlank(requestForm.getDeptId())) {
            predicates.add(cb.equal(root.get("dept"), requestForm.getDeptId()));
        }
        if (requestForm.getHasSig() != null) {
            if (requestForm.getHasSig()) {
                predicates.add(cb.isNotNull(sigJoin.get("fileName")));
            } else {
                predicates.add(cb.isNull(sigJoin.get("fileName")));
            }
        }
        selectQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        selectQuery.orderBy(cb.asc(root.get("jobNumber")));

        selectQuery.select(cb.construct(EmployeeSignatureDTO.class
                , root.get("name")
                , root.get("jobNumber")
                , root.get("company")
                , root.get("dept")
                , root.get("position")
                , orgJoin.get("name")
                , sigJoin.get("id")
                , sigJoin.get("fileName")
                )
        );


        return entityManager.createQuery(selectQuery).getResultList();

    }

}
