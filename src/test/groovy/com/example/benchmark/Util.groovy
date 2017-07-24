package com.example.benchmark

import org.junit.Assert

import java.util.concurrent.Callable
import java.util.function.Function
import java.util.stream.Collectors
/**
 *
 */
//@CompileStatic
class Util {

    static executeBenchmarks(String testName, Callable task) {
        execute(testName, task, [1, 2, 4, 8, 10, 16])
    }

    static executeBenchmarks(String testName, Callable task, List<Integer> multiThreadCase) {
        execute(testName, task, multiThreadCase)
    }

    private static List<BenchmarkReport> execute(String testName, Callable task, List<Integer> multiThreadCase) {
        multiThreadCase.collect { threadCount ->
            new BenchmarkReport(
                    duration: 30_000,
                    threadCount: threadCount,
                    checkSpeedInterval: 10_000,
                    logInIntervalsEnabled: false)
        }.each { report ->
            BenchmarkSuite.executeBenchmark(report, [("$testName" as String): task])
        }
    }

    static BenchmarkReport prepareReport() {
        new BenchmarkReport(
                duration: 30_000,
                threadCount: 1,
                checkSpeedInterval: 10_000,
                logInIntervalsEnabled: false)
    }

    static KeyValueEditInfo determineKeysForEdit(Map<String, String> operation, int percentsOfFieldsForEdit) {
        int editFieldsCount = operation.size() / 100 * percentsOfFieldsForEdit
        int deleteInsertFieldsCount = editFieldsCount / 3
        int updateFieldsCount = editFieldsCount - deleteInsertFieldsCount

        def keyValueEditInfo = new KeyValueEditInfo()
        def iterator = operation.entrySet().iterator()

        deleteInsertFieldsCount.times {
            def entry = iterator.next()
            keyValueEditInfo.keysToDelete[entry.key] = entry.value
        }
        keyValueEditInfo.keysToInsert = keyValueEditInfo.keysToDelete

        updateFieldsCount.times {
            def entry = iterator.next()
            keyValueEditInfo.keysToUpdate[entry.key] = entry.value
        }
        keyValueEditInfo
    }

    static KeyValueEditInfo determineKeysForEditOneUpdate(Map<String, String> operation, int percentsOfFieldsForEdit) {
        int editFieldsCount = operation.size() / 100 * percentsOfFieldsForEdit
        int deleteInsertFieldsCount = editFieldsCount / 3
        int updateFieldsCount = editFieldsCount - (deleteInsertFieldsCount * 2)

        def keyValueEditInfo = new KeyValueEditInfo()
        def iterator = operation.entrySet().iterator()

        deleteInsertFieldsCount.times {
            def entry = iterator.next()
            keyValueEditInfo.keysToDelete[entry.key] = entry.value
            iterator.next()
            keyValueEditInfo.keysToDelete[entry.key] = entry.value
        }

        updateFieldsCount.times {
            def entry = iterator.next()
            keyValueEditInfo.keysToUpdate[entry.key] = entry.value
        }
        keyValueEditInfo
    }

    static <T> void testReadWrite(List<T> initialOperationsData,
                                  Function<T, Long> writeOperation,
                                  Function<Long, T> readOperation) {
        List<Long> ids = initialOperationsData
                .stream()
                .map(writeOperation).collect(Collectors.toList());

        List<T> dbOperationsData = ids
                .stream()
                .map(readOperation)
                .collect(Collectors.toList())

        Assert.assertEquals(initialOperationsData, dbOperationsData);
    }

    static class KeyValueEditInfo {
        public Map<String, String> keysToDelete = [:]
        public Map<String, String> keysToInsert = [:]
        public Map<String, String> keysToUpdate = [:]
    }
}
