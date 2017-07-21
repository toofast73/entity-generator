package com.example.benchmark.oracle

import com.example.Start
import com.example.benchmark.BenchmarkSuite
import com.example.benchmark.ReadWriteEdit
import com.example.dao.oracle.ReaderService
import com.example.dao.oracle.WriterService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.Callable

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
@CompileStatic
@Slf4j
class EditSpeedBenchmark extends ReadWriteEdit {

    @Autowired
    private ReaderService readerService
    @Autowired
    private WriterService writerService

    @Test
    void testKeyValue() {

        int i = 0
        [5, 10, 30, 50, 70, 90].each { percentsOfFieldsForEdit ->
            [20, 100, 500, 10_000].each { fieldsCount ->

                List<Long> ids = readerService.loadKeyValueOperationIds(fieldsCount)
                Assert.assertFalse("Operations not found in DB", ids.isEmpty())

                BenchmarkSuite.executeBenchmark(prepareReport(),
                        [("Edit $percentsOfFieldsForEdit% fields in KeyValue table, with $fieldsCount fields in doc" as String): {

                            Long id = ids[++i % 10]
                            Map<String, String> operation = readerService.readKeyValueOperation(id)
                            def editInfo = determineKeysForEdit(operation, percentsOfFieldsForEdit)
                            writerService.editKeyValueOperation(id, editInfo.keysToDelete, editInfo.keysToInsert, editInfo.keysToUpdate)

                        } as Callable])
            }
        }
    }

    @Test
    void testChunks() {

        int i = 0
        [20: 1, 100: 2, 500: 7, /*10_000: 134*/ 10_000: 137].each { fieldsCount, chunksCount ->

            List<Long> ids = readerService.loadChunkOperationIds(chunksCount)
            Assert.assertFalse("Operations not found in DB", ids.isEmpty())

            Map<Long, String> operations = ids.collectEntries { id ->
                [(id as Long): readerService.readChunkOperation(id)]
            }

            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Edit in Chunk table, with $fieldsCount fields in doc" as String): {

                        long id = ids[++i % 10]
                        writerService.editChunkedOperation(id, operations[id])

                    } as Callable])
        }
    }
}