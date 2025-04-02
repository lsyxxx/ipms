package com.abt.common.listener;

import com.abt.common.model.User;
import com.abt.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * jpa List<User> 转换
 */
@Converter
public class JpaListUserConverter implements AttributeConverter<List<User>, String> {
    @Override
    public String convertToDatabaseColumn(List<User> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            return JsonUtil.toJson(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing List<User> to json", e);
        }
    }

    @Override
    public List<User> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        ObjectMapper objectMapper = JsonUtil.ObjectMapper();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<User>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing List<User> to json", e);
        }
    }
}
