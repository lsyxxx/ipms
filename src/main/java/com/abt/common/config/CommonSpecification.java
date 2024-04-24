package com.abt.common.config;

import com.abt.common.model.RequestForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static com.abt.common.util.QueryUtil.like;

/**
 * RequestForm 通用
 */
public class CommonSpecification<T extends RequestForm, E> {

    public Specification<E> entityIdLike(T form) {
        return (root, query, builder) -> {
            if (form.getId() != null) {
                return builder.like(root.get("id"), like(form.getId()));
            }
            return null;
        };
    }

    public Specification<E> afterStartDate(T form) {
        return (root, query, builder) -> {
            if (form.getStartDate() != null) {
                return builder.greaterThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getStartDate()));
            }
            return null;
        };
    }

    public Specification<E> beforeEndDate(T form) {
        return (root, query, builder) -> {
            if (form.getEndDate() != null) {
                return builder.lessThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getEndDate()));
            }
            return null;
        };
    }

    public Specification<E> typeEqual(T form, String typeAttributeName) {
        return (root, query, builder) -> {
            if (StringUtils.isNotBlank(form.getType())) {
                return builder.equal(root.get(typeAttributeName), form.getType());
            }
            return null;
        };
    }
}
