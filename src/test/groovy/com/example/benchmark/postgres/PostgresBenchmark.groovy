package com.example.benchmark.postgres

import com.example.Start
import com.example.dao.postgres.PostgresBenchmarkService
import com.example.data.filereader.JsonLoader
import com.example.data.filereader.KeyValueLoader
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
class PostgresBenchmark {

    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private KeyValueLoader keyValueLoader
    @Autowired
    private PostgresBenchmarkService postgresBenchmarkService;

    @Test
    void testWriteKeyValue() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> postgresBenchmarkService.createKeyValueOperation(operation)
                }
            } as Callable)
        }
    }

    @Test
    void testWriteChunks() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<String> operations = jsonLoader.load(fieldsCount)
            executeBenchmarks("Write in chunks, $fieldsCount fields", {
                operations.collect {
                    operation -> postgresBenchmarkService.createChunkedOperation(operation)
                }
            } as Callable)
        }
    }

    @Test
    void testWriteJson() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<String> operations = jsonLoader.load(fieldsCount)
            executeBenchmarks("Write in chunks, $fieldsCount fields", {
                operations.collect {
                    operation -> postgresBenchmarkService.writeJsonb(operation)
                }
            } as Callable)
        }
    }

    @Test
    void testReadKeyValue() {
        int i = 0
        [20, 100, 500, 10_000].each { fieldsCount ->
            List<Long> ids = postgresBenchmarkService.loadKeyValueOperationIds(fieldsCount)

            executeBenchmarks("Read in key value, $fieldsCount fields", {

                postgresBenchmarkService.readKeyValueOperation(ids[++i % 10])


            } as Callable)
        }
    }

    @Test
    void testReadChunks() {
        int i = 0
        [20: 1, 100: 2, 500: 7, 10_000: 134/*10_000: 137*/].each { fieldsCount, chunksCount ->
            List<Long> ids = postgresBenchmarkService.loadChunkOperationIds(chunksCount)

            executeBenchmarks("Read in chunks, $fieldsCount fields", {

                postgresBenchmarkService.readChunkOperation(ids[++i % 10])

            } as Callable)
        }
    }
}