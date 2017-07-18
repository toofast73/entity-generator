package com.example.benchmark

import com.example.Start
import com.example.cassandra.CassandraBenchmarkService
import com.example.dao.WriterService
import com.example.data.converter.JsonToKeyValueConverter
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
    @Autowired
    private JsonToKeyValueConverter jsonToKeyValueConverter

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

            List<String> operations = jsonLoader.load(fieldsCount)
            executeBenchmarks("Write in chunks, $fieldsCount fields", {
                operations.collect {
                    operation -> writerService.createChunkedOperation(operation)
                }
            } as Callable)
        }
    }

    @Test
    void testCassandraMap() {
        cassandraBenchmarkService.createBenchmarkTable()


        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeBenchmarkMapToMap(operation)
                }
            } as Callable)

        }

        cassandraBenchmarkService.dropBenchmarkTable()
    }

    @Test
    void testCassandraKeyValue() {
        jsonToKeyValueConverter.cqlMode() //todo switch off

        Map<String, String> pattern = keyValueLoader.load(20).get(0) //todo single map
        cassandraBenchmarkService.createBenchmarkTable(pattern)
        [20].each { fieldsCount ->
            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeBenchmarkMapToTable(operation)
                }
            } as Callable)
        }
        cassandraBenchmarkService.dropBenchmarkTable()

        pattern = keyValueLoader.load(100).get(0) //todo single map
        cassandraBenchmarkService.createBenchmarkTable(pattern)
        [100].each { fieldsCount ->
            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeBenchmarkMapToTable(operation)
                }
            } as Callable)
        }
        cassandraBenchmarkService.dropBenchmarkTable()
    }
}