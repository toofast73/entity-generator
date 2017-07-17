package com.example.benchmark

import com.example.Start
import com.example.dao.ReaderService
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
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
class ReadSpeedBenchmark {
    private static Log log = LogFactory.getLog(ReadSpeedBenchmark.class)

    @Autowired
    private ReaderService readerService

    @Test
    void testKeyValue() {

        int i = 0
        [20, 100, 500, 10_000].each { fieldsCount ->
            List<Long> ids = readerService.loadKeyValueOperationIds(fieldsCount)

            executeBenchmarks("Read in key value, $fieldsCount fields", {

                int opIdx = ++i % 10
                readerService.readKeyValueOperation(ids.get(opIdx))


            } as Callable)
        }
    }

    @Test
    void testChunks() {

        int i = 0
        [20: 1, 100: 2, 500: 7, 10_000: 137].each { fieldsCount, chunksCount ->
            List<Long> ids = readerService.loadChunkOperationIds(chunksCount)

            executeBenchmarks("Read in chunks, $fieldsCount fields", {

                int opIdx = ++i % 10
                readerService.readChunkOperation(ids.get(opIdx))

            } as Callable)
        }
    }
}
