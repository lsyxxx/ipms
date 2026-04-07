package com.abt.chkmodule;

import com.abt.chkmodule.entity.CheckComponent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

/**
 * {@link CheckComponent} 列表与数据库 JSON 文本互转
 */
@Converter
public class CheckComponentListConverter implements AttributeConverter<List<CheckComponent>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final TypeReference<List<CheckComponent>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(List<CheckComponent> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化 CheckComponent 列表失败", e);
        }
    }

    @Override
    public List<CheckComponent> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(dbData, TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("反序列化 CheckComponent 列表失败", e);
        }
    }
}
