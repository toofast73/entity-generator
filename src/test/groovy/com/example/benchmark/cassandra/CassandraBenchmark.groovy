package com.example.benchmark.cassandra

import com.example.Start
import com.example.cassandra.CassandraBenchmarkService
import com.example.dao.IdGenerator
import com.example.dao.ReadWrite
import com.example.dao.ReaderService
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
import static java.lang.Math.toIntExact

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
class CassandraBenchmark extends ReadWrite{
    private static Log log = LogFactory.getLog(CassandraBenchmark.class)

    @Autowired
    private WriterService writerService
    @Autowired
    private ReaderService readerService
    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private KeyValueLoader keyValueLoader
    @Autowired
    private CassandraBenchmarkService cassandraBenchmarkService
    @Autowired
    private JsonToKeyValueConverter jsonToKeyValueConverter
    @Autowired
    private IdGenerator generator;

    @Test
    void testReadWriteKeyValue() throws Exception {

        cassandraBenchmarkService.createBenchmarkTable()

        List<Map<String, String>> initialOperationsData = keyValueLoader.loadAll()

        testReadWrite(initialOperationsData,
                {operationData -> cassandraBenchmarkService.writeBenchmarkMapToMap(operationData)},
                {operationId -> cassandraBenchmarkService.readBenchmarkMapTable(operationId)}
        )

        cassandraBenchmarkService.dropBenchmarkTable();
    }

    @Test
    void testWriteReadMap() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            cassandraBenchmarkService.createBenchmarkTable()

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeBenchmarkMapToMap(operation)
                }
            } as Callable)

            long id = generator.counter.get()
            Random random = new Random()

            executeBenchmarks("Read in key value, $fieldsCount fields", {

                random.nextInt(toIntExact(id))
                cassandraBenchmarkService.readBenchmarkMapTable(String.valueOf(id))

            } as Callable)
            cassandraBenchmarkService.dropBenchmarkTable()
        }
    }

    @Test
    void testWriteKeyValue() {
        jsonToKeyValueConverter.cqlMode(true)

        [20, 100, 500, 10_000].each { fieldsCount ->
            Map<String, String> pattern = keyValueLoader.load(fieldsCount).get(0)
            cassandraBenchmarkService.createBenchmarkTable(pattern)
            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeBenchmarkMapToTable(operation)
                }
            } as Callable)
            cassandraBenchmarkService.dropBenchmarkTable()
        }
    }
}