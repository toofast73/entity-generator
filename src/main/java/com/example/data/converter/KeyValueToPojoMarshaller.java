package com.example.data.converter;

import java.util.Map;

/**
 *
 */
interface KeyValueToPojoMarshaller {

    <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType);
}
