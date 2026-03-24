package com.abt.chkmodule;

import com.abt.chkmodule.entity.Instrument;
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
 * List<Instrument> converter
 */
@Converter(autoApply = true)
@Slf4j
public class InstrumentListConverter implements AttributeConverter<List<Instrument>, String> {
    @Override
    public String convertToDatabaseColumn(List<Instrument> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new SystemException("Error while serializing List<Instrument> to json", e);
        }
    }

    @Override
    public List<Instrument> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Instrument>>() {});
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new SystemException("Error while deserializing List<Instrument> to json", e);
        }
    }
}
