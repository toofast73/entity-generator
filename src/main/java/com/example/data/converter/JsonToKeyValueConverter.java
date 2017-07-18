package com.example.data.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.beanutils.PropertyUtils.getIndexedProperty;
import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyType;
import static org.apache.commons.beanutils.PropertyUtils.setIndexedProperty;

@Service
public class JsonToKeyValueConverter {

    @Autowired
    private JacksonConverter jacksonConverter;

    private String POINT = ".";
    private String OPEN = "[";
    private String CLOSE = "]";

    public Map<String, String> convertTo(String json) {

        Map<String, String> map = new HashMap<>();
        addKeys("", jacksonConverter.readTree(json), map);
        return map;
    }

    public <T> T convertFrom(Map<String, String> keyValue, Class<T> valueType) {
        try {

            PojoTray tray = new PojoTray();
            tray.setPojo(newInstance(valueType));
            int depth = 0;

            for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                Iterator<String> fields = Arrays.asList(entry.getKey().split("\\.")).iterator();

                String value = entry.getValue();
                String field = fields.next();

                if (isIndexedField(field) && depth == 0) {
                    setProperty(tray, fields, PojoTray.POJO_FIELD_NAME + field, valueType, value, ++depth);
                } else {
                    setProperty(tray.getPojo(), fields, field, valueType, value, ++depth);
                }
            }
            return (T) tray.getPojo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void setProperty(Object bean, Iterator<String> fields, String field, Class<T> valueType, String value, int depth) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        if (isIndexedField(field)) {
            setIndexedProperty(bean, field, newInstance(valueType.getComponentType()));
            Object fieldValue = getIndexedProperty(bean, field);
            setProperty(fieldValue, fields, fields.next(), getPropertyType(bean, field), value, ++depth);

        } else if (isObject(valueType)) {
            PropertyUtils.setProperty(bean, field, newInstance(valueType));
            Object fieldValue = getProperty(bean, field);
            setProperty(fieldValue, fields, fields.next(), getPropertyType(bean, field), value, ++depth);

        } else {
            PropertyUtils.setProperty(bean, field, value);
        }
    }

    private <T> boolean isObject(Class<T> valueType) {
        return !valueType.isPrimitive()
                && !valueType.isAssignableFrom(String.class);
    }

    private <T> T newInstance(Class<T> valueType) throws IllegalAccessException, InstantiationException {

        if (valueType.isAssignableFrom(List.class)) {
            return (T) new ArrayList();
        } else if (valueType.isAssignableFrom(Set.class)) {
            return (T) new HashSet();
        } else if (valueType.isAssignableFrom(Map.class)) {
            return (T) new HashMap();
        } else if (valueType.isArray()) {
            return (T) Array.newInstance(valueType.getComponentType(), 0);
        } else {
            return valueType.newInstance();
        }
    }

    public static class PojoTray {
        private static String POJO_FIELD_NAME = "pojo";
        private Object pojo;

        public Object getPojo() {
            return pojo;
        }

        public void setPojo(Object pojo) {
            this.pojo = pojo;
        }
    }

    private static boolean isIndexedField(String field) {
        return field.matches(".*\\[\\d+\\]");
    }

    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + POINT;

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                addKeys(currentPath + OPEN + i + CLOSE, arrayNode.get(i), map);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.asText());
        }
    }

    /**
     * в названии колонок не может быть . и [ ]
     */
    public void cqlMode() {
        POINT = "POINT";
        OPEN = "OPEN";
        CLOSE = "CLOSE";
    }
}