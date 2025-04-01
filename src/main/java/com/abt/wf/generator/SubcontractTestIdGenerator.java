package com.abt.wf.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Year;

/**
 * 分包测试ID生成器
 * 生成格式：年度+FB+4位数字流水号
 * 例如：2024FB0001
 */
@Component
public class SubcontractTestIdGenerator implements IdentifierGenerator {

    private static final String PREFIX = "FB";


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        // 获取当前年份
        String year = String.valueOf(Year.now().getValue());

        // 查询当前年份的最大流水号
        String queryStr = "SELECT MAX(e.id) FROM SubcontractTesting e WHERE e.id LIKE :yearPrefix";
        SelectionQuery<String> query = session.createSelectionQuery(queryStr, String.class);
        query.setParameter("yearPrefix", year + PREFIX + "%");
        String lastId = query.getSingleResult();

        // 计算新的流水号
        int nextNumber = 1;
        if (lastId != null) {
            String lastNumberStr = lastId.substring((year + PREFIX).length());
            nextNumber = Integer.parseInt(lastNumberStr) + 1;
        }

        // 生成新的 ID
        if (nextNumber > 9999) {
            return String.format("%s%s%d", year, PREFIX, nextNumber);
        } else {
            return String.format("%s%s%04d", year, PREFIX, nextNumber);
        }

    }
}
