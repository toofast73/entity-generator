package com.example.data.converter;

import com.example.data.pojo.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PojoToKeyValueConverter implements Converter<Operation[], Map<String, String>> {

    @Autowired
    private JacksonConverter jacksonConverter;
    @Autowired
    private JsonToKeyValueConverter jsonToKeyValueConverter;

    @Override
    public Map<String, String> convertTo(Operation[] pojo) {

        String json = jacksonConverter.toJson(pojo);
        return jsonToKeyValueConverter.convertTo(json);
    }

    @Override
    public Operation[] convertFrom(Map<String, String> keyValue) {

        String json = jsonToKeyValueConverter.convertFrom(keyValue);
        return jacksonConverter.fromJson(json, Operation[].class);
    }
}