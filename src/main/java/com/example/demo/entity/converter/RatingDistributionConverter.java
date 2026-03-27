package com.example.demo.entity.converter;

import com.example.demo.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Converter
public class RatingDistributionConverter implements AttributeConverter<Map<String, Long>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<LinkedHashMap<String, Long>> MAP_TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(Map<String, Long> attribute) {
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute == null ? defaultDistribution() : attribute);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Khong the luu phan bo danh gia");
        }
    }

    @Override
    public Map<String, Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return defaultDistribution();
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, MAP_TYPE);
        } catch (IOException e) {
            throw new BusinessException("Khong the doc phan bo danh gia");
        }
    }

    private Map<String, Long> defaultDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (int rating = 1; rating <= 5; rating++) {
            distribution.put(String.valueOf(rating), 0L);
        }
        return distribution;
    }
}
