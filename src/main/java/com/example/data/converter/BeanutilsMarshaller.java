package com.example.data.converter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.springframework.stereotype.Service;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyType;

/**
 *
 */
@Service
class BeanutilsMarshaller implements KeyValueToPojoMarshaller {

    private Resolver resolver = new DefaultResolver();

    @Override
    public <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType) {
        try {

            PojoTray tray = new PojoTray();

            for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                Iterator<String> fields = asList(entry.getKey().split("\\.")).iterator();

                String value = entry.getValue();
                String field = fields.next();

                if (resolver.isIndexed(field)) {
                    setProperty(tray, fields, PojoTray.POJO_FIELD_NAME + field, valueType, value);
                } else {
                    setProperty(tray.getPojo(), fields, field, valueType, value);
                }
            }
            return (T) tray.getPojo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void setProperty(Object bean, Iterator<String> fields, String field, Class<T> valueType, String dataValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

//        if (bean == null) {
//            throw new IllegalArgumentException("Bean cannot be null");
//        }
//        if (StringUtils.isBlank(field)) {
//            throw new IllegalArgumentException("Field cannot be blank");
//        }
//        if (valueType == null) {
//            throw new IllegalArgumentException("Value type cannot be null");
//        }

        if (isObject(valueType)) {
            Object objValue = prepareObject(bean, field, valueType);

            if (objValue == null) { // контейнер (лист, массив, мапа, сет) с данными
                setDataValue(bean, field, String.class, dataValue);
                return;
            }

            if (!fields.hasNext()) {
                return;
            }
            String subField = fields.next();
            subField = FIELD_MAPPINGS.getOrDefault(subField, subField);
            Class<?> subFieldType = getPropertyType(objValue, subField);
            setProperty(objValue, fields, subField, subFieldType, dataValue);

        } else {
            setDataValue(bean, field, valueType, dataValue);
        }
    }

    private <T> Object prepareObject(Object bean, String objField, Class<T> objType) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Object objValue;
        if (resolver.isIndexed(objField)) {
            checkIndexedContainer(bean, objType, objField);
            objValue = getProperty(bean, objField);
            if (objValue == null) {
                Type parameterType = objType.getComponentType() != null
                        ? objType.getComponentType()
                        : (objType.getGenericSuperclass() != null ?
                        ((ParameterizedType) objType.getGenericSuperclass()).getActualTypeArguments()[0]
                        : null);
                if (parameterType != null) {
                    objValue = newInstance((Class<T>) parameterType);
                    PropertyUtils.setProperty(bean, objField, objValue);
                }
            }
        } else {
            objValue = getProperty(bean, objField);
            if (objValue == null) {
                objValue = newInstance(objType);
                PropertyUtils.setProperty(bean, objField, objValue);
            }
        }
        return objValue;
    }

    private <T> void setDataValue(Object bean, String field, Class<T> valueType, String dataValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyEditor editor = PropertyEditorManager.findEditor(valueType);
        editor.setAsText(dataValue);
        PropertyUtils.setProperty(bean, field, editor.getValue());
    }

    private <T> void checkIndexedContainer(Object bean, Class<T> valueType, String indexedField) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        int idx = resolver.getIndex(indexedField);
        String field = resolver.getProperty(indexedField);
        Object value = getProperty(bean, field);
        if (value == null) {
            value = newInstance(valueType);
            PropertyUtils.setProperty(bean, field, value);
        }
        if (valueType.isArray()) {
            Object[] arr = (Object[]) value;
            if (arr.length < idx + 1) {
                PropertyUtils.setProperty(bean, field, copyOf(arr, idx + 1));
            }
        } else if (List.class.isAssignableFrom(valueType)) {
            List<T> list = (List<T>) value;
            while (list.size() < idx + 1) {
                list.add(null);
            }
        }
    }

    private <T> boolean isObject(Class<T> valueType) {
        return !valueType.isPrimitive()
                && !valueType.isAssignableFrom(String.class)
                && !valueType.isAssignableFrom(Date.class)
                && !valueType.isAssignableFrom(BigDecimal.class)
                && !valueType.isAssignableFrom(BigInteger.class)
                && !valueType.isAssignableFrom(Double.class)
                && !valueType.isAssignableFrom(Float.class)
                && !valueType.isAssignableFrom(Long.class)
                && !valueType.isAssignableFrom(Integer.class)
                && !valueType.isAssignableFrom(Short.class)
                && !valueType.isAssignableFrom(Byte.class)
                && !valueType.isAssignableFrom(Character.class)
                && !valueType.isAssignableFrom(Boolean.class)
                ;
    }

    private static <T> T newInstance(Class<T> valueType) throws IllegalAccessException, InstantiationException {

        if (List.class.isAssignableFrom(valueType)) {
            return (T) new ArrayList();
        } else if (Set.class.isAssignableFrom(valueType)) {
            return (T) new HashSet();
        } else if (Map.class.isAssignableFrom(valueType)) {
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
}
