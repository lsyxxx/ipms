package com.abt.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnabledRepositoryImpl<T, ID extends Serializable> implements EnabledRepository<T, ID> {

    private final EntityManager em;
    private final JpaEntityInformation<T, ID> entityInformation;

    public EnabledRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    private void checkEnabledField() {
        try {
            entityInformation.getJavaType().getDeclaredField("enabled");
        } catch (NoSuchFieldException e) {
            throw new UnsupportedOperationException(
                    entityInformation.getJavaType().getSimpleName() + " 没有 enabled 字段，不能使用 EnabledRepository");
        }
    }

    private String entityName() {
        return entityInformation.getEntityName();
    }

    private String idProperty() {
        return entityInformation.getRequiredIdAttribute().getName();
    }

    @Override
    @Transactional
    public void enableById(ID id) {
        checkEnabledField();
        String jpql = "UPDATE " + entityName() + " e SET e.enabled = true WHERE e." + idProperty() + " = :id";
        em.createQuery(jpql).setParameter("id", id).executeUpdate();
    }

    @Override
    @Transactional
    public void disableById(ID id) {
        checkEnabledField();
        String jpql = "UPDATE " + entityName() + " e SET e.enabled = false WHERE e." + idProperty() + " = :id";
        em.createQuery(jpql).setParameter("id", id).executeUpdate();
    }

    @Override
    @Transactional
    public void disableAllByIds(Iterable<ID> ids) {
        bulkSetEnabled(ids, false);
    }

    @Override
    @Transactional
    public void enableAllByIds(Iterable<ID> ids) {
        bulkSetEnabled(ids, true);
    }

    private void bulkSetEnabled(Iterable<ID> ids, boolean enabled) {
        checkEnabledField();
        List<ID> list = toList(ids);
        if (list.isEmpty()) {
            return;
        }
        String jpql = "UPDATE " + entityName() + " e SET e.enabled = :enabled WHERE e." + idProperty() + " IN :ids";
        em.createQuery(jpql)
                .setParameter("enabled", enabled)
                .setParameter("ids", list)
                .executeUpdate();
    }

    private static <ID> List<ID> toList(Iterable<ID> ids) {
        if (ids == null) {
            return List.of();
        }
        List<ID> list = new ArrayList<>();
        for (ID id : ids) {
            list.add(id);
        }
        return list;
    }
}
