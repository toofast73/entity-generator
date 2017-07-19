package com.example.dao;

import org.junit.Assert;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReadWrite {

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
