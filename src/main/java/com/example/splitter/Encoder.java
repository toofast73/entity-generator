package com.example.splitter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;

/**
 *
 */
public enum Encoder {

    HEX(new ByteArrayEncoder() {
        @Override
        public byte[] decode(String text) {
            try {
                return Hex.decodeHex(text.toCharArray());
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String encode(byte[] bytes) {
            return Hex.encodeHexString(bytes);
        }
    }),
    BASE64_JAVA_UTIL(new ByteArrayEncoder() {
        @Override
        public byte[] decode(String text) {
            return java.util.Base64.getDecoder().decode(text);
        }

        @Override
        public String encode(byte[] bytes) {
            return java.util.Base64.getEncoder().encodeToString(bytes);
        }
    }),
    BASE64_COMMONS(new ByteArrayEncoder() {
        @Override
        public byte[] decode(String text) {
            return Base64.decodeBase64(text);
        }

        @Override
        public String encode(byte[] bytes) {
            return Base64.encodeBase64String(bytes);
        }
    }),
    BINARY_COMMONS(new ByteArrayEncoder() {
        @Override
        public byte[] decode(String text) {
            return BinaryCodec.fromAscii(text.toCharArray());
        }

        @Override
        public String encode(byte[] bytes) {
            return BinaryCodec.toAsciiString(bytes);
        }
    });

    private final ByteArrayEncoder encoder;

    Encoder(ByteArrayEncoder encoder) {
        this.encoder = encoder;
    }

    public ByteArrayEncoder getInstance() {
        return encoder;
    }
}
