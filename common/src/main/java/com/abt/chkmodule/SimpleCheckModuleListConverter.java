package com.abt.chkmodule;

import com.abt.chkmodule.model.SimpleCheckModule;
import com.abt.sys.exception.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * List<SimpleCheckModule> 转成json字符串
 */
@Converter(autoApply = true)
@Slf4j
public class SimpleCheckModuleListConverter implements AttributeConverter<List<SimpleCheckModule>, String> {
    @Override
    public String convertToDatabaseColumn(List<SimpleCheckModule> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new SystemException("Error while serializing List<SimpleCheckModule> to json", e);
        }
    }

    @Override
    public List<SimpleCheckModule> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<SimpleCheckModule>>() {});
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new SystemException("Error while deserializing List<SimpleCheckModule> to json", e);
        }
    }
}
