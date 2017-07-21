package com.example.data.converter;

import com.example.data.pojo.OperationData;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 *
 */
@Service
class JacksonMapMarshaller implements KeyValueToPojoMarshaller {

    private Resolver resolver = new DefaultResolver();
    @Autowired
    private JacksonMarshaller jacksonMarshaller;

    @Override
    public <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType) {
        try {

            Map<String, Object> map = new LinkedHashMap<>();
            List<Object> opList = new ArrayList<>();
            map.put("operations", opList);

            for (Map.Entry<String, String> entry : keyValue.entrySet()) {

                Iterator<String> fields = asList(entry.getKey().split("\\.")).iterator();
                addValueToList(opList, fields, fields.next(), entry.getValue());
            }

            OperationData operationData = jacksonMarshaller.map2Object(map, OperationData.class);
            return (T) operationData.getOperations();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addValueToList(List<Object> list, Iterator<String> fields, String field, String value) throws InstantiationException, IllegalAccessException {

        boolean isLastField = !fields.hasNext();
        int index = resolver.getIndex(field);
        if (isLastField) {
            int lastIdx = list.size() - 1;
            if (lastIdx < index) {
                for (int i = lastIdx + 1; i <= index; i++) {
                    if (i == index) {
                        list.add(value);
                    } else {
                        list.add(null);
                    }
                }
            } else {
                list.set(index, value);
            }
            return;
        }

        String nextField = fields.next();
        String nextProperty = resolver.getProperty(nextField);
        if (StringUtils.isBlank(nextProperty)) {

            List<Object> subList = prepareContainer(list, index, ArrayList.class);
            addValueToList(subList, fields, nextField, value);
        } else {
            Map<String, Object> subMap = prepareContainer(list, index, LinkedHashMap.class);
            addValueToMap(subMap, fields, nextField, value);
        }
    }

    private void addValueToMap(Map<String, Object> map, Iterator<String> fields, String field, String value) throws IllegalAccessException, InstantiationException {

        int index = resolver.getIndex(field);
        if (index >= 0) {
            String property = resolver.getProperty(field);
            List<Object> subList = (List<Object>) map.computeIfAbsent(property, key -> new ArrayList<>());
            addValueToList(subList, fields, field.substring(field.indexOf('['), field.length()), value);
            return;
        }

        boolean isLastField = !fields.hasNext();
        if (isLastField) {
            field = TO_FIELD_MAPPINGS.getOrDefault(field, field);
            map.put(field, value);
            return;
        }

        Map<String, Object> subMap = new LinkedHashMap<>();
        map.put(field, subMap);
        addValueToMap(subMap, fields, fields.next(), value);
    }

    private <T> T prepareContainer(List<Object> list, int index, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T cont;
        int lastIdx = list.size() - 1;
        if (lastIdx < index) {
            cont = clazz.newInstance();

            for (int i = lastIdx + 1; i <= index; i++) {
                if (i == index) {
                    list.add(cont);
                } else {
                    list.add(null);
                }
            }
        } else {
            cont = (T) list.get(index);
            if (cont == null) {
                cont = clazz.newInstance();
                list.set(index, cont);
            }
        }
        return cont;
    }
}
