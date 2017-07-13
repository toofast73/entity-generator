package com.example.data.filereader;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
}
