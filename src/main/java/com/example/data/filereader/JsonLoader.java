package com.example.data.filereader;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 */
@Service
public class JsonLoader {

    public List<String> loadAll() {

        return loadFiles(new String[]{
                "/data/json/01.01.01_20f.json",
                "/data/json/01.01.02_100f.json",
                "/data/json/01.01.03_500f.json",
                "/data/json/01.01.04_10000f.json",
                "/data/json/01.01.05.json",
        });
    }

    public List<String> load_1_20f() {
        return loadFiles(new String[]{
                "/data/json/01.01.01_20f.json"
        });
    }

    public List<String> load_1_100f() {
        return loadFiles(new String[]{
                "/data/json/01.01.02_100f.json"
        });
    }

    public List<String> load_1_500f() {
        return loadFiles(new String[]{
                "/data/json/01.01.03_500f.json"
        });
    }

    public List<String> load_1_10000f() {
        return loadFiles(new String[]{
                "/data/json/01.01.04_10000f.json"
        });
    }

    private List<String> loadFiles(String[] files) {
        return Arrays.stream(files)
                .map(FileUtil::readFile)
                .collect(Collectors.toList());
    }
}
