package com.example.benchmark.oracle

import com.example.Start
import com.example.dao.oracle.ReaderService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.Callable

import static com.example.benchmark.Util.executeBenchmarks

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
@CompileStatic
@Slf4j
class ReadSpeedBenchmark {

    @Autowired
    private ReaderService readerService

    @Test
    void testKeyValue() {

        int i = 0
        [20, 100, 500, 10_000].each { fieldsCount ->
            List<Long> ids = readerService.loadKeyValueOperationIds(fieldsCount)
            Assert.assertFalse("Operations not found in DB", ids.isEmpty())

            executeBenchmarks("Read in key value, $fieldsCount fields", {

                readerService.readKeyValueOperation(ids[++i % 10])

            } as Callable)
        }
    }

    @Test
    void testChunks() {


        int i = 0
        [20: 1, 100: 2, 500: 7, 10_000: 134/*10_000: 137*/].each { fieldsCount, chunksCount ->
            List<Long> ids = readerService.loadChunkOperationIds(chunksCount)
            Assert.assertFalse("Operations not found in DB", ids.isEmpty())

            executeBenchmarks("Read in chunks, $fieldsCount fields", {

                readerService.readChunkOperation(ids[++i % 10])

            } as Callable)
        }
    }
}