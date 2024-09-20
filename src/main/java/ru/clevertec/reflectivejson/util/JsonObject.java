package ru.clevertec.reflectivejson.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonObject {
    private final Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public boolean has(String key) {
        return map.containsKey(key);
    }

    public String getString(String key) {
        return (String) map.get(key);
    }

    public UUID getUUID(String key) {
        return UUID.fromString((String) map.get(key));
    }

    public OffsetDateTime getOffsetDateTime(String key) {
        return OffsetDateTime.parse((String) map.get(key), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public LocalDate getLocalDate(String key) {
        return LocalDate.parse((String) map.get(key));
    }

    public JsonArray getJsonArray(String key) {
        return (JsonArray) map.get(key);
    }

    public JsonObject getJsonObject(String key) {
        return (JsonObject) map.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\": ");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof JsonObject || entry.getValue() instanceof JsonArray) {
                sb.append(entry.getValue().toString());
            } else {
                sb.append(entry.getValue());
            }
            sb.append(", ");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}
