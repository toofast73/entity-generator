package com.example.data.converter;

import com.example.data.pojo.Operation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PojoToKeyValueConverter implements Converter<Operation[], Map<String, String>> {
    private static Log log = LogFactory.getLog(PojoToKeyValueConverter.class);

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

        Operation[] obj = jsonToKeyValueConverter.convertFrom(keyValue, Operation[].class);
        log.info("Prepared POJO: " + jacksonConverter.toJson(obj));
        return obj;
    }
}