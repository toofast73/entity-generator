package com.example.data.filereader;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 *
 */
public class KeyValueFileReader {

    public List<Map<String, String>> load() {

        return Arrays.stream(
                new String[]{
                        "/data/key_value/01.01.01.properties",
                        "/data/key_value/01.01.02.properties",
                        "/data/key_value/01.01.03.properties",
                        "/data/key_value/01.01.04.properties",
                        "/data/key_value/01.01.05.properties",
                }).map(KeyValueFileReader::readPropertyFile)
                .collect(Collectors.toList());
    }

    private static Map<String, String> readPropertyFile(String filepath) {

        Properties properties = new Properties();

        try (InputStream stream = new ClassPathResource(filepath).getInputStream()) {

            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> map = new HashMap<>();
        properties.entrySet().forEach(entry -> {
            map.put((String) entry.getKey(), (String) entry.getValue());
        });

        return map;
    }
}
