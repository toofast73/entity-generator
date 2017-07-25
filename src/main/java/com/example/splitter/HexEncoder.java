package com.example.splitter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 */
public class HexEncoder implements ByteArrayEncoder {

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
}
