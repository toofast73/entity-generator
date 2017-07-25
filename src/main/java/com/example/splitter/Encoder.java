package com.example.splitter;

/**
 *
 */
public enum Encoder {

    HEX(new HexEncoder()),
    BASE64(new Base64Encoder());

    private final ByteArrayEncoder encoder;

    Encoder(ByteArrayEncoder encoder) {
        this.encoder = encoder;
    }

    public ByteArrayEncoder getEncoder() {
        return encoder;
    }
}
