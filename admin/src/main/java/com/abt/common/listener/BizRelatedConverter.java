package com.abt.common.listener;

import com.abt.common.model.BizRelatedVO;
import com.abt.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * 关联业务
 */
@Converter(autoApply = true)
public class BizRelatedConverter implements AttributeConverter<List<BizRelatedVO>, String> {


    @Override
    public String convertToDatabaseColumn(List<BizRelatedVO> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            return JsonUtil.toJson(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing List<BizRelatedVO> to json", e);
        }
    }

    @Override
    public List<BizRelatedVO> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        ObjectMapper objectMapper = JsonUtil.ObjectMapper();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<BizRelatedVO>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing List<BizRelatedVO> to json", e);
        }
    }
}
