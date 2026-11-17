package com.abt.testing.converter;

import com.abt.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/** 封面列表与 JSON 文本转换；空列表保存为 []，不改变其他模块转换规则。 */
@Converter
public class ReportCoverStringListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return JsonUtil.toJson(attribute == null ? List.of() : attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("报告封面列表序列化失败", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        List<String> result = JsonUtil.toObject(dbData, new TypeReference<List<String>>() {});
        return result == null ? new ArrayList<>() : result;
    }
}
