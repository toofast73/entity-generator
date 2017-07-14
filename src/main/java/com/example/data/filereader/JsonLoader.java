package com.example.data.filereader;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class JsonLoader {

    public List<String> loadAll() {

        return loadFiles(new String[]{
                "/data/json/01.01.01_20f.json",
                "/data/json/01.01.02.json",
                "/data/json/01.01.03.json",
                "/data/json/01.01.04.json",
                "/data/json/01.01.05.json",
        });
    }

    public List<String> load_1_20f() {
        return loadFiles(new String[]{
                "/data/json/01.01.01_20f.json"
        });
    }

    private List<String> loadFiles(String[] files) {
        return Arrays.stream(
                files).map(FileUtil::readFile)
                .collect(Collectors.toList());
    }
}
