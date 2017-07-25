package com.example.splitter;

import com.google.common.base.Splitter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
class CompressingSplitter implements Chunker {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private int level = Deflater.DEFAULT_COMPRESSION;

    @Override
    public Stream<String> split(String data, int chunkLength) {

        return StreamSupport.stream(
                Splitter.fixedLength(chunkLength)
                        .split(
                                Base64.getEncoder()
                                        .encodeToString(
                                                compress(data.getBytes(CHARSET), level)
                                        )
                        ).spliterator(), false);
    }

    public String merge(List<String> chunks) {
        return chunks.stream()
                .map(chunk -> {

                    byte[] bytes = Base64.getDecoder().decode(chunk);
                    return new String(decompress(bytes), CHARSET);

                }).collect(Collectors.joining());
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

    private static class SpecifiedLevelGZIPOutputStream extends GZIPOutputStream {
        private SpecifiedLevelGZIPOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        private void setLevel(int level) {
            def.setLevel(level);
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
