package com.abt.sys.repository;

import com.abt.sys.model.entity.Org;

import java.util.List;

public interface OrgRepository {

    List<Org> findAllByParentIdOrderByCascadeId(String parentId);
}