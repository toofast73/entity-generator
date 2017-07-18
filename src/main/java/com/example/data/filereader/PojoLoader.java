package com.example.data.filereader;

import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class PojoLoader {

    private final JsonLoader jsonLoader;

    public PojoLoader(JsonLoader jsonLoader) {
        this.jsonLoader = jsonLoader;
    }


}
