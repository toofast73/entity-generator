package com.example.data.converter;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 *
 */
interface KeyValueToPojoMarshaller {

    Map<String, String> FROM_FIELD_MAPPINGS = ImmutableMap.of("_id", "id");
    Map<String, String> TO_FIELD_MAPPINGS = ImmutableMap.of("id", "_id");

    <T> T fromKeyValue(Map<String, String> keyValue, Class<T> valueType);
}
