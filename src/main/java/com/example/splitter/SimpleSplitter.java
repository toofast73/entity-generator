package com.example.splitter;

import com.google.common.base.Splitter;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 */
//@Service
class SimpleSplitter implements Chunker {

    @Override
    public Stream<String> split(String data, int chunkLength) {
        return StreamSupport.stream(
                Splitter.fixedLength(chunkLength)
                        .split(data).spliterator(), false);
    }
}
