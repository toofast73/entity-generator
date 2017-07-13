package com.example.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JacksonMapper {

    ObjectMapper mapper = new ObjectMapper();

    // Convert object to JSON string
    public String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    // Convert JSON string to Object
    public Object fromJson(String jsonInString, Class<?> valueType) throws IOException {
        return mapper.readValue(jsonInString, valueType);
    }

    // Convert object to map string
    public <K,V> Map<K, V> toMap(Object object) throws JsonProcessingException {
        return mapper.convertValue(object, new TypeReference<Map<K, V>>() {});
    }

    // Convert map to object string
    public Object map2Object(Map<Object, Object> map, Class<?> valueType) throws IOException {
        return mapper.readValue(mapper.writeValueAsString(map), valueType);
    }

    // Convert map to JSON string
    public String fromMap(Map<Object, Object> map) throws IOException {
        return mapper.writeValueAsString(map);
    }

 }