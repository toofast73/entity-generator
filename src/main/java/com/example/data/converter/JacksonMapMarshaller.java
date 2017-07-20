package com.example.data.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 */
@Service
class JacksonMapMarshaller implements KeyValueToPojoMarshaller {

    @Autowired
    private JacksonMarshaller jacksonMarshaller;

    @Override
    public <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType) {
        try {


            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
