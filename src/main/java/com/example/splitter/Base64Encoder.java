package com.example.splitter;

import java.util.Base64;

/**
 *
 */
public class Base64Encoder implements ByteArrayEncoder {

    @Override
    public byte[] decode(String text) {
        return Base64.getDecoder().decode(text);
    }

    @Override
    public String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
