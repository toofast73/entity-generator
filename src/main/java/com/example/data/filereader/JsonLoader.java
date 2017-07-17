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
                "/data/json/01.01.01_100f.json",
                "/data/json/01.01.01_500f.json",
                "/data/json/01.01.01_10000f.json",
                "/data/json/01.01.02.json",
        });
    }

    public List<String> load(int fieldsCount) {
        return loadFiles(new String[]{
                "/data/json/01.01.01_" + fieldsCount + "f.json"
        });
    }

    private List<String> loadFiles(String[] files) {
        return Arrays.stream(files)
                .map(FileUtil::readFile)
                .collect(Collectors.toList());
    }
}
