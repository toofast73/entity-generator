package com.example.dao;

import com.example.Start;
import com.example.data.filereader.JsonLoader;
import com.example.data.filereader.KeyValueLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
//@Transactional  // с анннотацией данные в БД откатываются после прогона
public class ReadWriteTest {

    @Autowired
    private WriterService writerService;
    @Autowired
    private ReaderService readerService;
    @Autowired
    private JsonLoader jsonLoader;
    @Autowired
    private KeyValueLoader keyValueLoader;

    @Test
    public void testKeyValue() throws Exception {

        List<Map<String, String>> initialOperationsData = keyValueLoader.loadAll();

        testReadWrite(initialOperationsData,
                operationData -> writerService.createKeyValueOperation(operationData),
                operationId -> readerService.readKeyValueOperation(operationId)
        );
    }

    @Test
    public void testChunks() throws Exception {

        List<String> initialOperationsData = jsonLoader.loadAll();

        testReadWrite(initialOperationsData,
                operationData -> writerService.createChunkedOperation(operationData),
                operationId -> readerService.readChunkedOperation(operationId)
        );
    }

    private <T> void testReadWrite(List<T> initialOperationsData,
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
