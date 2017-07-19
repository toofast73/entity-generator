package com.example.dao.cassandra;

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
        Class<?> aClass = field.getType();
        String name = field.getName();
        doWith(aClass, name);
    }

    public void doWith(Map.Entry<String, Class> entry) throws IllegalArgumentException, IllegalAccessException {
        String name = entry.getKey();
        Class aClass = entry.getValue();
        doWith(aClass, name);
    }


    public void doWith(Map<String, Class> classMap) {
        classMap.entrySet().forEach(entry -> {
            try {
                doWith(entry);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void doWith(Class<?> aClass, String name) {
        if (isComplex(aClass)) {
            path.push(name);
            doWithFields(aClass, this);
            path.pop();
        } else {
            path.push(name);
            fields.put(join(DELIMITER, path), aClass);
            path.pop();
        }
    }

    // TODO: 14/07/2017
    private boolean isComplex(Class aClass) {
        return false;
//        return aClass.getName().startsWith("com.example");
    }

}
