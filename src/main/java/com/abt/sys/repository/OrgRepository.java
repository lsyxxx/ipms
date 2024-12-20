package com.abt.sys.repository;

import com.abt.sys.model.entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrgRepository extends JpaRepository<Org, String> {

    List<Org> findAllByParentIdOrderByCascadeId(String parentId);
}