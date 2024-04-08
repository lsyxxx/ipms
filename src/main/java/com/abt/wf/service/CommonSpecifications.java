package com.abt.wf.service;

import com.abt.common.model.RequestForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import static com.abt.common.util.QueryUtil.like;

/**
 * 通用的搜索条件，RequestForm的常用搜索
 * T: requestForm
 * R: return
 */
public class CommonSpecifications<T extends RequestForm, R> {

    public Specification<R> entityIdLike(T form) {
        return (root, query, builder) -> {
            if (form.getStartDate() != null || form.getEndDate() != null) {
                return builder.between(root.get("id"), form.getStartDate(), form.getId());
            }
            return null;
        };
    }

    public Specification<R> createDateBetween(T form) {
        return (root, query, builder) -> {
            if (form.getStartDate() != null || form.getEndDate() != null) {
                return builder.between(root.get("createDate"), form.getStartDate(), form.getEndDate());
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
            if (StringUtils.isNotBlank(form.getUsername())) {
                return builder.like(root.get("createUserid"), form.getUserid());
            }
            return null;
        };
    }

    public Specification<R> stateEqual(T form) {
        return (root, query, builder) -> {
            if (StringUtils.isNotBlank(form.getState())) {
                return builder.equal(root.get("state"), form.getState());
            }
            return null;
        };
    }

    public Specification<R> isNotDelete(T form) {
        return (root, query, builder) -> builder.isFalse(root.get("isDelete"));
    }
}
