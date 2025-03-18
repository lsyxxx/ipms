package com.abt.common.listener;

import com.abt.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * jps实体中json转为List<String>类型
 */
@Converter
public class JpaListStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return JsonUtil.toJson(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing List<String> to json", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        ObjectMapper objectMapper = JsonUtil.ObjectMapper();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing List<String> to json", e);
        }
    }
}
