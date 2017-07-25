package com.example.splitter;

import java.util.stream.Stream;

/**
 *
 */
public interface Chunker {

    Stream<String> split(String data, int chunkLength);
}
