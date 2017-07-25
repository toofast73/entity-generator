package com.example.splitter;

/**
 *
 */
public interface ByteArrayEncoder {

    byte[] decode(String text);

    String encode(byte[] bytes);
}
