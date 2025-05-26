package com.abt.safety.converter;

import com.abt.common.util.JsonUtil;
import com.abt.safety.entity.SafetyItem;
import com.abt.sys.model.entity.SystemFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * json SafetyItem转换器
 */
@Converter
public class SafetyItemConverter implements AttributeConverter<SafetyItem, String> {
    @Override
    public String convertToDatabaseColumn(SafetyItem attribute) {
        try {
            if (attribute == null) {
                return null;
            }
            return JsonUtil.toJson(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing SafetyItem to json", e);
        }
    }

    @Override
    public SafetyItem convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = JsonUtil.ObjectMapper();
        try {
            return objectMapper.readValue(dbData, new TypeReference<SafetyItem>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing SafetyItem to json", e);
        }
    }
}
