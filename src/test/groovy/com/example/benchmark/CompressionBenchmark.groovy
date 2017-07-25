package com.example.benchmark

import com.example.Start
import com.example.data.filereader.JsonLoader
import com.example.splitter.CompressingChunker
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.math.MathContext
import java.util.concurrent.Callable
import java.util.zip.Deflater

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
@CompileStatic
@Slf4j
class CompressionBenchmark {

    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private CompressingChunker chunker

    @Test
    void testJsonCompression() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<String> jsons = jsonLoader.load(fieldsCount)

            ["DEFAULT_COMPRESSION": Deflater.DEFAULT_COMPRESSION,
             "BEST_SPEED"         : Deflater.BEST_SPEED,
             "BEST_COMPRESSION"   : Deflater.BEST_COMPRESSION
            ].each { levelName, level ->

                chunker.setLevel(level)
                def coeff = new BigDecimal(chunker.calcCompressionCoefficient(jsons[0]), new MathContext(2))

                BenchmarkSuite.executeBenchmark(prepareReport(),
                        [("Compress JSON, $fieldsCount fields, compression level $levelName, Compression coefficient $coeff" as String): {
                            jsons.collect {
                                json -> chunker.split(json, 4000)
                            }
                        } as Callable])
            }
        }
    }

    private static BenchmarkReport prepareReport() {
        new BenchmarkReport(
                duration: 10_000,
                threadCount: 1,
                checkSpeedInterval: 10_000,
                logInIntervalsEnabled: false)
    }
}
