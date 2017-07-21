package com.example.benchmark

import org.junit.Assert

import java.util.function.Function
import java.util.stream.Collectors

class ReadWriteEdit {
    protected static BenchmarkReport prepareReport() {
        new BenchmarkReport(
                duration: 30_000,
                threadCount: 1,
                checkSpeedInterval: 10_000,
                logInIntervalsEnabled: false)
    }

    protected KeyValueEditInfo determineKeysForEdit(Map<String, String> operation, int percentsOfFieldsForEdit) {
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

    static class KeyValueEditInfo {
        public Map<String, String> keysToDelete = [:]
        public Map<String, String> keysToInsert = [:]
        public Map<String, String> keysToUpdate = [:]
    }

    protected <T> void testReadWrite(List<T> initialOperationsData,
                                     Function<T, Long> writeOperation,
                                     Function<Long, T> readOperation) {
        List<Long> ids = initialOperationsData
                .stream()
                .map(writeOperation).collect(Collectors.toList());

        List<T> dbOperationsData = ids
                .stream()
                .map(readOperation)
                .collect(Collectors.toList());

        Assert.assertEquals(initialOperationsData, dbOperationsData);
    }
}
