package com.example.mapper;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static java.lang.String.join;
import static org.springframework.util.ReflectionUtils.doWithFields;

public class FieldCollector implements ReflectionUtils.FieldCallback {

    private final Stack<String> path = new Stack<>();

    private Map<String, Class> fields = new HashMap<>();
    private static final String DELIMITER = "_";

    public Map<String, Class> getFields() {
        return fields;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

        if (isComplex(field)) {
            path.push(field.getName());
            doWithFields(field.getType(), this);
            path.pop();
        } else {
            path.push(field.getName());
            fields.put(join(DELIMITER, path), field.getType());
            path.pop();
        }
    }

    // TODO: 14/07/2017
    private boolean isComplex(Field field) {
        return false;
//        return field.getType().getName().startsWith("com.example");
    }
}
