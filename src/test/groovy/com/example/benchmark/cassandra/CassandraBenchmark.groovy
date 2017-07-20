package com.example.benchmark.cassandra

import com.example.Start
import com.example.dao.ReadWrite
import com.example.dao.cassandra.CassandraBenchmarkService
import com.example.dao.oracle.IdGenerator
import com.example.dao.oracle.ReaderService
import com.example.dao.oracle.WriterService
import com.example.data.converter.KeyValueMarshaller
import com.example.data.converter.PojoConverter
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
class CassandraBenchmark extends ReadWrite {
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
    private PojoConverter converter
    @Autowired
    private IdGenerator generator
    @Autowired
    private KeyValueMarshaller keyValueMarshaller

    @Test
    void testReadWriteKeyValue() throws Exception {

        cassandraBenchmarkService.create()

        List<Map<String, String>> initialOperationsData = keyValueLoader.loadAll()

        testReadWrite(initialOperationsData,
                { operationData -> cassandraBenchmarkService.writeMapAsMap(operationData) },
                { operationId -> cassandraBenchmarkService.read(operationId) }
        )

        cassandraBenchmarkService.drop();
    }

    @Test
    void testWriteReadMap() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            cassandraBenchmarkService.create()

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeMapAsMap(operation)
                }
            } as Callable)

            long id = generator.counter.get()
            Random random = new Random()
            executeBenchmarks("Read in Map, $fieldsCount fields", {

                cassandraBenchmarkService.read(String.valueOf(random.nextInt((Integer) id)))

            } as Callable)
            cassandraBenchmarkService.drop()
        }
    }

    @Test
    void testWriteReadKeyValue() {
        converter.cqlMode(true)

        [20, 100, 500, 10_000].each { fieldsCount ->
            Map<String, String> pattern = keyValueLoader.load(fieldsCount).get(0)
            cassandraBenchmarkService.create(pattern)
            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeMapAsKeyValue(operation)
                }
            } as Callable)

            long id = generator.counter.get()
            Random random = new Random()
            executeBenchmarks("Read in key value, $fieldsCount fields", {

                cassandraBenchmarkService.read(String.valueOf(random.nextInt((Integer) id)))

            } as Callable)

            cassandraBenchmarkService.drop()
        }
    }

}