package com.example.data.filereader;

import com.example.data.converter.JacksonMarshaller;
import com.example.data.pojo.Operation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class PojoLoader implements FileLoader<Operation[]> {

    private final JsonLoader jsonLoader;
    private final JacksonMarshaller jacksonMarshaller;

    public PojoLoader(JsonLoader jsonLoader, JacksonMarshaller jacksonMarshaller) {
        this.jsonLoader = jsonLoader;
        this.jacksonMarshaller = jacksonMarshaller;
    }

    @Override
    public List<Operation[]> loadAll() {
        return jsonLoader.loadAll().stream()
                .map(json -> {
                    return (Operation[]) jacksonMarshaller.fromJson(json, Operation[].class);
                })

                .collect(Collectors.toList());
    }

    @Override
    public List<Operation[]> load(int fieldsCount) {
        return jsonLoader.load(fieldsCount).stream()
                .map(json -> {
                    return (Operation[]) jacksonMarshaller.fromJson(json, Operation[].class);
                }).collect(Collectors.toList());
    }
}
