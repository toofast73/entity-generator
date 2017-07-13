package com.example.data.filereader;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
class FileUtil {

    static String readFile(String classPathResource) {

        try {
            return readFile(new ClassPathResource(classPathResource).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String readFile(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    static Map<String, String> readPropertyFile(String filepath) {

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
