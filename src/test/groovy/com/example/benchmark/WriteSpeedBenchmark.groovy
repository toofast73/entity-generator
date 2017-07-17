package com.example.benchmark

import com.example.Start
import com.example.cassandra.CassandraBenchmarkService
import com.example.dao.WriterService
import com.example.data.filereader.JsonLoader
import com.example.data.filereader.KeyValueLoader
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
class WriteSpeedBenchmark {
    private static Log log = LogFactory.getLog(WriteSpeedBenchmark.class)

    @Autowired
    private WriterService writerService
    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private KeyValueLoader keyValueLoader
    @Autowired
    private CassandraBenchmarkService cassandraBenchmarkService

    @Test
    void testKeyValue() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> writerService.createKeyValueOperation(operation)
                }
            } as Callable)
        }
    }

    @Test
    void testChunks() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<String> operations20 = jsonLoader.load(fieldsCount)
            executeBenchmarks("Write in chunks, $fieldsCount fields", {
                operations20.collect {
                    operation -> writerService.createChunkedOperation(operation)
                }
            } as Callable)
        }
    }

    @Test
    void testCassandraKeyValue() {
        cassandraBenchmarkService.createBenchmarkMapTable()


        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeBenchmarkMapTable(operation)
                }
            } as Callable)

        }

        cassandraBenchmarkService.dropBenchmarkMapTable()
    }
}