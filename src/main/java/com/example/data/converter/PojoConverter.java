package com.example.data.converter;

import com.example.data.pojo.Operation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PojoConverter {
    private static Log log = LogFactory.getLog(PojoConverter.class);

    @Autowired
    private JacksonMarshaller jacksonMarshaller;
    @Autowired
    private KeyValueMarshaller keyValueMarshaller;

    public Map<String, String> convertPojoToKeyValue(Operation[] pojo) {
        return keyValueMarshaller.toKeyValue(convertPojoToJson(pojo));
    }

    public String convertPojoToJson(Operation[] pojo) {
        return jacksonMarshaller.toJson(pojo);
    }

    public Operation[] convertJsonToPojo(String json) {
        return jacksonMarshaller.fromJson(json, Operation[].class);
    }

    public Map<String, String> convertJsonToKeyValue(String json) {
        return keyValueMarshaller.toKeyValue(json);
    }

    public Operation[] convertKeyValueToPojo(Map<String, String> keyValue) {
        return keyValueMarshaller.fromKeyValue(keyValue, Operation[].class);
    }

    public void cqlMode(boolean b) {
        keyValueMarshaller.cqlMode(b);
    }
}