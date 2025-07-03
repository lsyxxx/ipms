package com.abt.market;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Optional;

/**
 * 结算单号生成器
 */
@Component
public class SettlementIdGenerator implements IdentifierGenerator {
    public static final String CODE = "JS";
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        String year = String.valueOf(Year.now().getValue());
        // 查询当前年份的最大流水号
        String entityName = object.getClass().getSimpleName();
        String query = String.format("SELECT MAX(CAST(SUBSTRING(id, %d) AS LONG)) FROM %s WHERE id LIKE :prefix",
                year.length() + CODE.length() + 1, entityName);
        Long maxSequence = Optional.ofNullable(
                        session.createQuery(query, Long.class)
                                .setParameter("prefix", year + CODE + "%")
                                .uniqueResult())
                .orElse(0L);
        long nextSequence = maxSequence + 1;
        //超过4位
        if (nextSequence > 9999) {
            return String.format("%s%s%d", year, CODE, nextSequence);
        }

        return String.format("%s%s%04d", year, CODE, nextSequence);
    }
}
