package com.example.data.filereader;

import com.example.converter.JsonToKeyValueConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class KeyValueLoader {

    private final JsonLoader jsonLoader;
    private final JsonToKeyValueConverter jsonToKeyValueConverter;

    public KeyValueLoader(JsonLoader jsonLoader, JsonToKeyValueConverter jsonToKeyValueConverter) {
        this.jsonLoader = jsonLoader;
        this.jsonToKeyValueConverter = jsonToKeyValueConverter;
    }

    public List<Map<String, String>> loadAll() {
        return jsonLoader.loadAll().stream()
                .map(jsonToKeyValueConverter::convert)
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> load(int fieldsCount) {
        return jsonLoader.load(fieldsCount).stream()
                .map(jsonToKeyValueConverter::convert)
                .collect(Collectors.toList());
    }
}
