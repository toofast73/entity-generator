package com.example.data.converter;

import java.util.Map;

/**
 *
 */
interface PojoToKeyValueMarshaller {

    Map<String, String> toKeyValue(String json);
}
