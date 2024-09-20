package ru.clevertec.reflectivejson.util;

import java.util.ArrayList;
import java.util.List;

public class JsonArray {
    private final List<Object> list = new ArrayList<>();

    public void add(Object value) {
        list.add(value);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public int length() {
        return list.size();
    }

    public List<Object> toList() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Object value : list) {
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            sb.append(", ");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }
}
