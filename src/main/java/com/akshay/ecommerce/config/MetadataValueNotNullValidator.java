package com.akshay.ecommerce.config;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class MetadataValueNotNullValidator implements ConstraintValidator<MetadataValueNotNull, Map<String, Object>> {
    @Override
    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {
        if (metadata == null || metadata.isEmpty()) {
            return false;
        }
        Set<String> lowerCaseKeys = new HashSet<>();
        Set<String> normalizedValues = new HashSet<>();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.trim().isEmpty()) {
                return false;
            }
            String lowerKey = key.trim().toLowerCase();
            if (lowerCaseKeys.contains(lowerKey)) {
                return false;
            } else {
                lowerCaseKeys.add(lowerKey);
            }
            if (value == null) {
                return false;
            }
            if (value instanceof String strValue) {
                String trimmedValue = strValue.trim();
                if (trimmedValue.isEmpty()) {
                    return false;
                }
                String lowerValue = trimmedValue.toLowerCase();
                if (lowerValue.equals("null") || lowerValue.equals("undefined")) {
                    return false;
                }
                if (trimmedValue.length() > 100) {
                    return false;
                }
                if (normalizedValues.contains(lowerValue)) {
                    return false;
                } else {
                    normalizedValues.add(lowerValue);
                }
            }
        }
        return true;
    }
}
