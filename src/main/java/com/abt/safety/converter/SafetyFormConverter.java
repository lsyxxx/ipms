package com.abt.safety.converter;

import com.abt.common.util.JsonUtil;
import com.abt.safety.entity.SafetyForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

/**
 * SafetyForm <-> json
 */
@Converter(autoApply = true)
public class SafetyFormConverter implements AttributeConverter<SafetyForm, String> {


    @Override
    public String convertToDatabaseColumn(SafetyForm attribute) {
        try {
            if (attribute == null) {
                return null;
            }
            return JsonUtil.toJson(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing SafetyForm to json", e);
        }
    }

    @Override
    public SafetyForm convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = JsonUtil.ObjectMapper();
        try {
            return objectMapper.readValue(dbData, new TypeReference<SafetyForm>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing SafetyForm to json", e);
        }
    }
}
