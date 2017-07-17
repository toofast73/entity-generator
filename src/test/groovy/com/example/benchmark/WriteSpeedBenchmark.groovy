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
//@Transactional  // с анннотацией данные в БД откатываются после прогона
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

        List<Map<String, String>> operations20 = keyValueLoader.load_1_20f()
        executeBenchmarks("Write in key value, 20 fields", {
            operations20.collect {
                operation -> writerService.createKeyValueOperation(operation)
            }
        } as Callable)

        List<Map<String, String>> operations100 = keyValueLoader.load_1_100f()
        executeBenchmarks("Write in key value, 100 fields", {
            operations100.collect {
                operation -> writerService.createKeyValueOperation(operation)
            }
        } as Callable)

        List<Map<String, String>> operations500 = keyValueLoader.load_1_500f()
        executeBenchmarks("Write in key value, 500 fields", {
            operations500.collect {
                operation -> writerService.createKeyValueOperation(operation)
            }
        } as Callable)

        List<Map<String, String>> operations10000 = keyValueLoader.load_1_10000f()
        executeBenchmarks("Write in key value, 10_000 fields", {
            operations10000.collect {
                operation -> writerService.createKeyValueOperation(operation)
            }
        } as Callable)
    }

    @Test
    void testChunks() {

        List<String> operations20 = jsonLoader.load_1_20f()
        executeBenchmarks("Write in chunks, 20 fields", {
            operations20.collect {
                operation -> writerService.createChunkedOperation(operation)
            }
        } as Callable)

        List<String> operations100 = jsonLoader.load_1_100f()
        executeBenchmarks("Write in chunks, 100 fields", {
            operations100.collect {
                operation -> writerService.createChunkedOperation(operation)
            }
        } as Callable)

        List<String> operations500 = jsonLoader.load_1_500f()
        executeBenchmarks("Write in chunks, 500 fields", {
            operations500.collect {
                operation -> writerService.createChunkedOperation(operation)
            }
        } as Callable)

        List<String> operations10000 = jsonLoader.load_1_10000f()
        executeBenchmarks("Write in chunks, 10_000 fields", {
            operations10000.collect {
                operation -> writerService.createChunkedOperation(operation)
            }
        } as Callable)
    }

    @Test
    void testCassandraKeyValue() {
        cassandraBenchmarkService.createBenchmarkMapTable()

        List<Map<String, String>> operations20 = keyValueLoader.load_1_20f()
        executeBenchmarks("Write in key value, 20 fields", {
            operations20.collect {
                operation -> cassandraBenchmarkService.writeBenchmarkMapTable(operation)
            }
        } as Callable)

        List<Map<String, String>> operations100 = keyValueLoader.load_1_100f()
        executeBenchmarks("Write in key value, 100 fields", {
            operations100.collect {
                operation -> cassandraBenchmarkService.writeBenchmarkMapTable(operation)
            }
        } as Callable)

        List<Map<String, String>> operations500 = keyValueLoader.load_1_500f()
        executeBenchmarks("Write in key value, 500 fields", {
            operations500.collect {
                operation -> cassandraBenchmarkService.writeBenchmarkMapTable(operation)
            }
        } as Callable)


        List<Map<String, String>> operations10000 = keyValueLoader.load_1_10000f()
        executeBenchmarks("Write in key value, 10_000 fields", {
            operations10000.collect {
                operation -> cassandraBenchmarkService.writeBenchmarkMapTable(operation)
            }
        } as Callable)

        cassandraBenchmarkService.dropBenchmarkMapTable();
    }
}