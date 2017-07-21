package com.example.data.converter;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 *
 */
//@Service
class JacksonMapMarshaller implements KeyValueToPojoMarshaller {

    @Autowired
    private JacksonMarshaller jacksonMarshaller;

    @Override
    public <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType) {
        try {

            //jacksonMarshaller.toMap()

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
