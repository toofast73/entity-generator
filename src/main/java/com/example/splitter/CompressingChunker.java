package com.example.splitter;

import com.google.common.base.Splitter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 */
@Service
public class CompressingChunker implements Chunker {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private int level = Deflater.DEFAULT_COMPRESSION;
    private ByteArrayEncoder encoder = Encoder.BASE64_JAVA_UTIL.getInstance();

    @Override
    public Stream<String> split(String data, int chunkLength) {

        return StreamSupport.stream(
                Splitter.fixedLength(chunkLength)
                        .split(
                                encoder.encode(
                                        compress(data.getBytes(CHARSET), level)
                                )
                        ).spliterator(), false);
    }

    public String merge(List<String> chunks) {
        return new String(
                decompress(
                        encoder.decode(
                                chunks.stream().collect(Collectors.joining())
                        )
                ), CHARSET);
    }

    public double calcCompressionCoefficient(String text) {
        return (double) text.length() /
                split(text, 10_000)
                        .collect(Collectors.joining())
                        .length();
    }

    private static byte[] compress(byte[] data, int level) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (SpecifiedLevelGZIPOutputStream gzip = new SpecifiedLevelGZIPOutputStream(baos)) {
            gzip.setLevel(level);
            gzip.write(data);
            gzip.finish();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static byte[] decompress(byte[] data) {
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buff = new byte[100_000];
            int read;
            while ((read = gzip.read(buff)) != -1) {
                baos.write(buff, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setEncoder(ByteArrayEncoder encoder) {
        this.encoder = encoder;
    }

    private static class SpecifiedLevelGZIPOutputStream extends GZIPOutputStream {
        private SpecifiedLevelGZIPOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        private void setLevel(int level) {
            def.setLevel(level);
        }
    }
}
