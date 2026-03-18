package com.abt.wf.service;

import com.abt.common.model.RequestForm;
import com.abt.common.util.TimeUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static com.abt.common.util.QueryUtil.like;

/**
 * 通用的搜索条件，RequestForm的常用搜索
 * T: requestForm
 * R: return
 */
public class CommonSpecifications<T extends RequestForm, R> {

    public Specification<R> entityIdLike(T form) {
        return (root, query, builder) -> {
            if (form.getId() != null) {
                return builder.like(root.get("id"), like(form.getId()));
            }
            return null;
        };
    }

    public Specification<R> afterStartDate(T form) {
        return (root, query, builder) -> {
            if (form.getStartDate() != null) {
                return builder.greaterThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getStartDate()));
            }
            return null;
        };
    }

    public Specification<R> beforeEndDate(T form) {
        return (root, query, builder) -> {
            if (form.getEndDate() != null) {
                return builder.lessThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getEndDate()));
            }
            return null;
        };
    }

    public Specification<R> createUsernameLike(T form) {
        return (root, query, builder) -> {
            if (StringUtils.isNotBlank(form.getUsername())) {
                return builder.like(root.get("createUsername"), like(form.getUsername()));
            }
            return null;
        };
    }

    public Specification<R> createUseridEqual(T form) {
        return (root, query, builder) -> {
            if (StringUtils.isNotBlank(form.getUserid())) {
                return builder.equal(root.get("createUserid"), form.getUserid());
            }
            return null;
        };
    }

    public Specification<R> stateEqual(T form) {
        return (root, query, builder) -> {
            if (StringUtils.isNotBlank(form.getState())) {
                return builder.equal(root.get("businessState"), form.getState());
            }
            return null;
        };
    }

    public Specification<R> isNotDelete(T form) {
        return (root, query, builder) -> builder.isFalse(root.get("isDelete"));
    }
}
