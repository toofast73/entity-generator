package com.example.data.filereader;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class JsonFileReader {

    public List<String> load() {

        return Arrays.stream(
                new String[]{
                        "/data/json/01.01.01.json",
                        "/data/json/01.01.02.json",
                        "/data/json/01.01.03.json",
                        "/data/json/01.01.04.json",
                        "/data/json/01.01.05.json",
                }).map(FileUtil::readFile)
                .collect(Collectors.toList());
    }
}
