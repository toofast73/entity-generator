package com.example.data.converter;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 *
 */
interface KeyValueToPojoMarshaller {

    Map<String, String> FIELD_MAPPINGS = ImmutableMap.of("_id", "id");

    <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType);
}
