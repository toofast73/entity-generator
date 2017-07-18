package com.example.data.filereader;

import com.example.data.converter.JacksonConverter;
import com.example.data.pojo.Operation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class PojoLoader {

    private final JsonLoader jsonLoader;
    private final JacksonConverter jacksonConverter;

    public PojoLoader(JsonLoader jsonLoader, JacksonConverter jacksonConverter) {
        this.jsonLoader = jsonLoader;
        this.jacksonConverter = jacksonConverter;
    }

    public <T> List<T> loadAll() {
        return jsonLoader.loadAll().stream()
                .map(json -> {
                    return (T) jacksonConverter.fromJson(json, Operation[].class);
                }).collect(Collectors.toList());
    }

    public <T> List<T> load(int fieldsCount) {
        return jsonLoader.load(fieldsCount).stream()
                .map(json -> {
                    return (T) jacksonConverter.fromJson(json, Operation[].class);
                }).collect(Collectors.toList());
    }
}
