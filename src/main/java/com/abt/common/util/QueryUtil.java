package com.abt.common.util;


import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.material.entity.MaterialDetail;
import com.abt.sys.exception.BusinessException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询工具
 */
@Slf4j
public class QueryUtil {

    public static final int NO_PAGING = 0;

    /**
     * 是否分页
     * @param limit 单页数量
     * @return: true: 分页
     *          false: 不分页
     */
    public static boolean isPaging(int limit) {
        return limit > NO_PAGING;
    }

    /**
     * sqlserver 分页sql
     * @param page 页数, 从1开始
     * @param size 单页数量
     */
    public static String pageSqlBySqlserver(int page, int size) {
        int skip = (page - 1) * size;
        return " offset " + skip + " rows fetch next " + size + " rows only ";
    }

    public static String like(String param) {
        return "%" + param + "%";
    }

    public static String sql(String sql) {
        return " " + sql + " ";
    }

    public static void ensureProperty(String prop, String propName) {
        if (StringUtils.isBlank(prop)) {
            throw new MissingRequiredParameterException("缺少必要参数(" + propName + ")");
        }
    }

    /**
     * 根据传入的properties 模糊查询
     * @param properties 查询属性
     * @param criteriaBuilder CriteriaBuilder criteriaBuilder
     * @param root Root<T> root
     * @return List<Predicate>
     */
    public static <T> List<Predicate> likeQueryPredicates(String query, List<String> properties, CriteriaBuilder criteriaBuilder, Root<T> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (properties == null || properties.isEmpty()) {
            return predicates;
        }
        properties.forEach(prop -> {
            try {
                predicates.add(criteriaBuilder.like(root.get(prop), QueryUtil.like(query)));
            } catch (IllegalArgumentException  e) {
                log.error(e.getMessage(),  e);
                throw new BusinessException("查询失败!实体" + root.getModel() + "中不包含" + prop + "属性");
            } catch (Exception e) {
                log.error(e.getMessage(),  e);
                throw new BusinessException("查询错误 - " + e.getMessage());
            }
        });
        return predicates;
    }

    /**
     * 根据传入的properties 排序
     * @param properties 传入的属性
     * @param criteriaBuilder CriteriaBuilder criteriaBuilder
     * @param root Root<T> root
     * @param isAscending 升序/降序
     */
    public static <T> List<Order> orderBy(List<String> properties, CriteriaBuilder criteriaBuilder, Root<T> root, boolean isAscending) {
        List<Order> orders = new ArrayList<>();
        if (properties == null || properties.isEmpty()) {
            return orders;
        }
        properties.forEach(prop -> {
            if (isAscending) {
                orders.add(criteriaBuilder.asc(root.get(prop)));
            }
        });
        return orders;

    }

}
