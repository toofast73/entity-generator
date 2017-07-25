package com.example.splitter

import groovy.util.logging.Slf4j
import org.junit.Test

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 *
 */
@Slf4j
class CompressingSplitterTest {

    CompressingSplitter splitter = new CompressingSplitter()

    @Test
    void testCompress() {

        def string = "Привет медвед, трали вали, привет"
        def encStr = splitter.split(string, 10).collect(Collectors.joining())
        def decStr = splitter.merge([encStr])

        assertEquals(string, decStr)
    }

    @Test
    void testCompressionCoefficient() {

        def string = """  123sd5678в9_  123456789_1235456789_  1256в3456789_12345ва6789_  1234356789_123456789_  12345j6789_
                123456789_  123sd sвd df789_12в3sd456789_  1235ва6456789_123456789_  12345в6789_1235в456789_  123456789_12367456789_  12673456789_1267
                3456789_  123456789_ dzgf zsd  asdfasdf sad fasdf asad
            1в23456789_ 1234567в89_1234в56789_  1234567895в_12345656789_  123564s dg6789_12gh3456789_  1234вghj56789_123gh764567в89_  1234560в789_
        """

        double coeff = splitter.calcCompressionCoefficient(string)
        log.info "Compression coefficient is $coeff"

        assertTrue(coeff > 1)
    }
}
