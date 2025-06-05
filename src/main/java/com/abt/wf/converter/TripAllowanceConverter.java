package com.abt.wf.converter;

import com.abt.common.util.JsonUtil;
import com.abt.wf.model.TripAllowanceDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 差旅报销-个人补助
 */
@Converter(autoApply = true)
public class TripAllowanceConverter implements AttributeConverter<List<TripAllowanceDetail>, String> {

    @Override
    public String convertToDatabaseColumn(List<TripAllowanceDetail> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            return JsonUtil.toJson(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing List<TripAllowanceDetail> to json", e);
        }
    }

    @Override
    public List<TripAllowanceDetail> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = JsonUtil.ObjectMapper();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<TripAllowanceDetail>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing List<TripAllowanceDetail> to json", e);
        }
    }
}
