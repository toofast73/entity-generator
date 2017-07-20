package com.example.benchmark.oracle

import com.example.Start
import com.example.benchmark.BenchmarkReport
import com.example.benchmark.BenchmarkSuite
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
class EditSpeedBenchmark {

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
        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Long> ids = readerService.loadChunkOperationIds(fieldsCount)
            Assert.assertFalse("Operations not found in DB", ids.isEmpty())
            Map<Long, String> operations = ids.collectEntries { id ->
                [id: readerService.readChunkOperation(id)]
            }

            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Edit in Chunk table, with $fieldsCount fields in doc" as String): {

                        long id = ids[++i % 10]
                        writerService.editChunkedOperation(id, operations[id])

                    } as Callable])
        }
    }

    private static BenchmarkReport prepareReport() {
        new BenchmarkReport(
                duration: 30_000,
                threadCount: 1,
                checkSpeedInterval: 10_000,
                logInIntervalsEnabled: false)
    }

    private KeyValueEditInfo determineKeysForEdit(Map<String, String> operation, int percentsOfFieldsForEdit) {
        def editFieldsCount = operation.size() / 100 * percentsOfFieldsForEdit
        def deleteInsertFieldsCount = editFieldsCount / 3
        def updateFieldsCount = editFieldsCount - deleteInsertFieldsCount

        def keyValueEditInfo = new KeyValueEditInfo()
        def iterator = operation.entrySet().iterator()

        deleteInsertFieldsCount.times {
            def entry = iterator.next()
            keyValueEditInfo.keysToDelete[entry.key] = entry.value
            keyValueEditInfo.keysToInsert[entry.key] = entry.value
        }

        updateFieldsCount.times {
            def entry = iterator.next()
            keyValueEditInfo.keysToUpdate[entry.key] = entry.value
        }
        keyValueEditInfo
    }

    private class KeyValueEditInfo {
        Map<String, String> keysToDelete = [:]
        Map<String, String> keysToInsert = [:]
        Map<String, String> keysToUpdate = [:]
    }
}