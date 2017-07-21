package com.example.benchmark.cassandra

import com.example.Start
import com.example.benchmark.BenchmarkSuite
import com.example.benchmark.ReadWriteEdit
import com.example.dao.oracle.IdGenerator
import com.example.dao.oracle.ReaderService
import com.example.dao.oracle.WriterService
import com.example.data.converter.PojoConverter
import com.example.data.filereader.JsonLoader
import com.example.data.filereader.KeyValueLoader
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.junit.Ignore
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
class CassandraBenchmark extends ReadWriteEdit {
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


    private static final Random random = new Random()

    @Test
    void testReadWriteMapEquals() throws Exception {

        cassandraBenchmarkService.create()

        List<Map<String, String>> initialOperationsData = keyValueLoader.loadAll()

        testReadWrite(initialOperationsData,
                { operationData -> cassandraBenchmarkService.writeMapAsMap(operationData) },
                { operationId -> cassandraBenchmarkService.readMap(operationId) }
        )

        cassandraBenchmarkService.drop();
    }

    @Test
    void testWriteReadMap() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            cassandraBenchmarkService.create()

            List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
            executeBenchmarks("Cassandra write map as cassandra map, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeMapAsMap(operation)
                }
            } as Callable, [2, 4, 8])

            long id = generator.counter.get()
            executeBenchmarks("Cassandra read cassandra map, $fieldsCount fields", {

                cassandraBenchmarkService.readMap(String.valueOf(random.nextInt((Integer) id)))

            } as Callable, [2, 4, 8])
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
            executeBenchmarks("Cassandra write map as key-value, $fieldsCount fields", {
                operations.collect {
                    operation -> cassandraBenchmarkService.writeMapAsKeyValue(operation)
                }
            } as Callable, [2, 4, 8])

            long id = generator.counter.get()
            executeBenchmarks("Cassandra read cassandra key-value in map, $fieldsCount fields", {

                cassandraBenchmarkService.read(String.valueOf(random.nextInt((Integer) id)), pattern)

            } as Callable, [2, 4, 8])

            cassandraBenchmarkService.drop()
        }
    }

    @Test
    void testKeyValue() {
        converter.cqlMode(true)

        [5, 10/*, 30, 50, 70, 90*/].each { percentsOfFieldsForEdit ->
            [20/*, 100, 500, 10_000*/].each { fieldsCount ->

                Map<String, String> pattern = keyValueLoader.load(fieldsCount).get(0)
                cassandraBenchmarkService.create(pattern)
                List<Map<String, String>> operations = keyValueLoader.load(fieldsCount)
                executeBenchmarks("Cassandra write map as key-value, $fieldsCount fields", {
                    operations.collect {
                        operation -> cassandraBenchmarkService.writeMapAsKeyValue(operation)
                    }
                } as Callable, [2, 4, 8])

                long index = generator.counter.get()
                executeBenchmarks("Cassandra read cassandra key-value in map, $fieldsCount fields", {

                    cassandraBenchmarkService.read(String.valueOf(random.nextInt((Integer) index)), pattern)

                } as Callable, [2, 4, 8])

                BenchmarkSuite.executeBenchmark(prepareReport(),
                        [("Edit $percentsOfFieldsForEdit% fields in KeyValue table, with $fieldsCount fields in doc" as String): {

                            String id = String.valueOf(random.nextInt((Integer) index))
                            Map<String, String> operation = cassandraBenchmarkService.read(id, pattern)
                            def editInfo = determineKeysForEdit(operation, percentsOfFieldsForEdit)
                            cassandraBenchmarkService.editMap(id, operation, editInfo)
                        } as Callable])


                cassandraBenchmarkService.drop()
            }
        }
    }


    //todo - тест не работает: не правильно создается таблица под JSON и не маппится наш JSON в нее
    @Ignore
    @Test
    void testWriteJson() {
        converter.cqlMode(true)

        [20/*, 100, 500, 10_000*/].each { fieldsCount ->

            List<String> operations = jsonLoader.load(fieldsCount)

            String jsonPattern = "{\"id\":\"\",\"array\":" + operations.get(0) + "}"

            Map<String, String> pattern = converter.convertJsonToKeyValue(jsonPattern)

            cassandraBenchmarkService.create(pattern)

            executeBenchmarks("Write in key value, $fieldsCount fields", {
                operations.collect {
                    operation ->
                        cassandraBenchmarkService.writeJson(
                                "{\"id\":\"\",\"array\":" + operation + "}"
                        )
                }
            } as Callable, [8])
            cassandraBenchmarkService.drop()
        }
    }
}