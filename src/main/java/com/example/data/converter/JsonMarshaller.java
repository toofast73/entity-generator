package com.example.data.converter;

/**
 *
 */
interface JsonMarshaller {

    /**
     * Convert POJO to JSON string
     */
    String toJson(Object o);

    /**
     * Convert JSON string to POJO
     */
    <T> T fromJson(String json, Class<T> valueType);

}
