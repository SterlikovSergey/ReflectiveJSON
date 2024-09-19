package ru.clevertec.reflectivejson.service;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
public class ToJsonConverter {
    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        StringBuilder json = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    json.append("\"").append(field.getName()).append("\":");
                    Object value = field.get(obj);
                    if (value == null) {
                        json.append("null");
                    } else if (value instanceof String || value instanceof UUID) {
                        json.append("\"").append(value).append("\"");
                    } else if (value instanceof OffsetDateTime) {
                        json.append("\"").append(((OffsetDateTime) value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).append("\"");
                    } else if (value instanceof LocalDate) {
                        json.append("\"").append(value.toString()).append("\"");
                    } else if (value instanceof List) {
                        json.append(toJson((List<?>) value));
                    } else if (value.getClass().isPrimitive() || value instanceof Number || value instanceof Boolean) {
                        json.append(value);
                    } else {
                        json.append(toJson(value));
                    }
                    json.append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        json.deleteCharAt(json.length() - 1).append("}");
        return json.toString();
    }

    private String toJson(List<?> list) {
        return list.stream()
                .map(this::toJson)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
