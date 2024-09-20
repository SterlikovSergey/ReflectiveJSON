package ru.clevertec.reflectivejson.service;

import org.springframework.stereotype.Service;
import ru.clevertec.reflectivejson.util.JsonArray;
import ru.clevertec.reflectivejson.util.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FromJsonConverter {
    public <T> T fromJson(String json, Class<T> clazz) throws Exception {
        JsonObject jsonObject = parseJsonObject(json);
        return fromJsonObject(jsonObject, clazz);
    }

    private JsonObject parseJsonObject(String json) {
        JsonObject jsonObject = new JsonObject();
        json = json.trim().substring(1, json.length() - 1);
        int braceCount = 0;
        int bracketCount = 0;
        StringBuilder keyValue = new StringBuilder();
        String key = null;
        boolean insideQuotes = false;

        for (char c : json.toCharArray()) {
            if (c == '\"') {
                insideQuotes = !insideQuotes;
            }
            if (!insideQuotes) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                if (c == '[') bracketCount++;
                if (c == ']') bracketCount--;
            }
            if (c == ',' && braceCount == 0 && bracketCount == 0 && !insideQuotes) {
                String[] pair = keyValue.toString().split(":", 2);
                key = pair[0].trim().replace("\"", "");
                String value = pair[1].trim();
                jsonObject.put(key, parseValue(value));
                keyValue.setLength(0);
            } else {
                keyValue.append(c);
            }
        }
        if (keyValue.length() > 0) {
            String[] pair = keyValue.toString().split(":", 2);
            key = pair[0].trim().replace("\"", "");
            String value = pair[1].trim();
            jsonObject.put(key, parseValue(value));
        }
        System.out.println("Parsed JsonObject: " + jsonObject);
        return jsonObject;
    }

    private JsonArray parseJsonArray(String json) {
        JsonArray jsonArray = new JsonArray();
        json = json.trim().substring(1, json.length() - 1);
        int braceCount = 0;
        int bracketCount = 0;
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : json.toCharArray()) {
            if (c == '\"') {
                insideQuotes = !insideQuotes;
            }
            if (!insideQuotes) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                if (c == '[') bracketCount++;
                if (c == ']') bracketCount--;
            }
            if (c == ',' && braceCount == 0 && bracketCount == 0 && !insideQuotes) {
                jsonArray.add(parseValue(value.toString().trim()));
                value.setLength(0);
            } else {
                value.append(c);
            }
        }
        if (value.length() > 0) {
            jsonArray.add(parseValue(value.toString().trim()));
        }
        System.out.println("Parsed JsonArray: " + jsonArray);
        return jsonArray;
    }

    private Object parseValue(String value) {
        if (value.startsWith("{")) {
            return parseJsonObject(value);
        } else if (value.startsWith("[")) {
            return parseJsonArray(value);
        } else if (value.startsWith("\"")) {
            return value.replace("\"", "");
        } else if (value.equals("null")) {
            return null;
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        } else {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return value;
            }
        }
    }

    private <T> T fromJsonObject(JsonObject jsonObject, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (jsonObject.has(fieldName)) {
                    Object value = jsonObject.get(fieldName);
                    if (value == null) {
                        field.set(obj, null);
                    } else if (field.getType().equals(String.class)) {
                        field.set(obj, jsonObject.getString(fieldName));
                    } else if (field.getType().equals(UUID.class)) {
                        field.set(obj, jsonObject.getUUID(fieldName));
                    } else if (field.getType().equals(OffsetDateTime.class)) {
                        field.set(obj, jsonObject.getOffsetDateTime(fieldName));
                    } else if (field.getType().equals(LocalDate.class)) {
                        field.set(obj, jsonObject.getLocalDate(fieldName));
                    } else if (field.getType().equals(List.class)) {
                        JsonArray jsonArray = jsonObject.getJsonArray(fieldName);
                        field.set(obj, fromJsonArray(jsonArray, field));
                    } else if (field.getType().isPrimitive()
                            || Number.class.isAssignableFrom(field.getType())
                            || Boolean.class.isAssignableFrom(field.getType())) {
                        field.set(obj, value);
                    } else {
                        field.set(obj, fromJsonObject(jsonObject.getJsonObject(fieldName), field.getType()));
                    }
                }
            }
        }
        System.out.println("Parsed Object: " + obj);
        return obj;
    }

    private List<?> fromJsonArray(JsonArray jsonArray, Field field) throws Exception {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JsonObject) {
                list.add(fromJsonObject((JsonObject) value, (Class<?>)
                        ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
            } else {
                list.add(value);
            }
        }
        return list;
    }
}
