package com.example.benchmark

import com.example.Start
import com.example.dao.oracle.ReaderService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
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

    @Test
    void testKeyValue() {

        int i = 0
        [5, 10, 30, 50, 70, 90].each { percentsOfFieldsForEdit ->
            [20, 100, 500, 10_000].each { fieldsCount ->

                List<Long> ids = readerService.loadKeyValueOperationIds(fieldsCount)
                BenchmarkSuite.executeBenchmark(prepareReport(),
                        [("Edit $percentsOfFieldsForEdit% fields in KeyValue table, with $fieldsCount fields in doc" as String): {

                            Map<String, String> operation = readerService.readKeyValueOperation(ids[++i % 10])
                            def editInfo = determineKeysForEdit(operation, percentsOfFieldsForEdit)


                        } as Callable])
            }
        }
    }

    @Test
    void testChunks() {

        int i = 0
        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Long> ids = readerService.loadChunkOperationIds(fieldsCount)
            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Edit in Chunk table, with $fieldsCount fields in doc" as String): {

                        String operation = readerService.readChunkOperation(ids[++i % 10])

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

    private KeyValueEditInfo determineKeysForEdit(Map<String, String> stringStringMap, int i) {

    }

    private class KeyValueEditInfo {
        Map<String, String> keysToDelete = new HashMap<>()
        Map<String, String> keysToInsert = new HashMap<>()
        Map<String, String> keysToUpdate = new HashMap<>()
    }
}