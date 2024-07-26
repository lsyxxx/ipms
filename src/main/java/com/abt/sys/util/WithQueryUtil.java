package com.abt.sys.util;

import com.abt.sys.model.WithQuery;
import java.util.List;

/**
 *
 */
public class WithQueryUtil {

    public static <T extends WithQuery<T>> T build(T entity) {
        return entity == null ? null : entity.afterQuery();
    }

    public static <T extends WithQuery<T>> List<T> build(List<T> queryList) {
        if (queryList != null && !queryList.isEmpty()) {
            queryList.forEach(WithQuery::afterQuery);
        }
        return queryList;
    }

}
