package com.abt.wf.generator;

import cn.idev.excel.util.StringUtils;
import com.abt.common.exception.MissingRequiredParameterException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.SelectionQuery;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Year;
import java.util.Optional;
import java.util.Properties;

/**
 * 通用ID生成
 * 年份+code+流水号，流水号是最大序号
 */
@Component
public class CommonIdGenerator implements IdentifierGenerator {

    private String code = "";

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) {
        // 从注解参数中获取自定义字符串
        this.code = parameters.getProperty("code", "");
        if (StringUtils.isBlank(this.code)) {
            throw new MissingRequiredParameterException("code");
        }

    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String year = String.valueOf(Year.now().getValue());
        // 查询当前年份的最大流水号
        String entityName = object.getClass().getSimpleName();
        String query = String.format("SELECT MAX(CAST(SUBSTRING(id, %d) AS LONG)) FROM %s WHERE id LIKE :prefix",
                year.length() + code.length() + 1, entityName);
        Long maxSequence = Optional.ofNullable(
                        session.createQuery(query, Long.class)
                                .setParameter("prefix", year + code + "%")
                                .uniqueResult())
                .orElse(0L);
        long nextSequence = maxSequence + 1;
        //超过4位
        if (nextSequence > 9999) {
            return String.format("%s%s%d", year, code, nextSequence);
        }

        return String.format("%s%s%04d", year, code, nextSequence);
    }
}
