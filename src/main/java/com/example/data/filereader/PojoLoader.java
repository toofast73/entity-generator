package com.example.data.filereader;

import com.example.data.converter.PojoConverter;
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
    private final PojoConverter converter;

    public PojoLoader(JsonLoader jsonLoader, PojoConverter converter) {
        this.jsonLoader = jsonLoader;
        this.converter = converter;
    }

    @Override
    public List<Operation[]> loadAll() {
        return jsonLoader.loadAll().stream()
                .map(converter::convertJsonToPojo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation[]> load(int fieldsCount) {
        return jsonLoader.load(fieldsCount).stream()
                .map(converter::convertJsonToPojo)
                .collect(Collectors.toList());
    }
}
