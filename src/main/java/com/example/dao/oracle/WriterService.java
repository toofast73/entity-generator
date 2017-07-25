package com.example.dao.oracle;

import com.example.dao.IdGenerator;
import com.example.splitter.Chunker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Service
@Transactional
public class WriterService {

    @Autowired
    private KeyValueDao keyValueDao;
    @Autowired
    private ChunkDao chunkDao;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private Chunker chunker;

    public long createKeyValueOperation(Map<String, String> data) {

        long recordId = keyValueDao.insertMain(
                idGenerator.generateId(), "SomeSystem", "Some operation");
        keyValueDao.insertChildren(recordId, data);
        return recordId;
    }

    public long createChunkedOperation(String json) {

        long recordId = chunkDao.insertMain(
                idGenerator.generateId(), "SomeSystem", "Some operation");

        AtomicInteger index = new AtomicInteger();

        insertChunkedChildren(json, recordId, index);
        return recordId;
    }

    public void editKeyValueOperation(long id,
                                      Map<String, String> keysToDelete,
                                      Map<String, String> keysToInsert,
                                      Map<String, String> keysToUpdate) {
        keyValueDao.deleteChildren(id, keysToDelete);
        keyValueDao.insertChildren(id, keysToInsert);
        keyValueDao.updateChildren(id, keysToUpdate);
    }

    public void editKeyValueOperation(long id, Map<String, String> keyValue) {
        keyValueDao.deleteChildren(id);
        keyValueDao.insertChildren(id, keyValue);
    }

    public void editChunkedOperation(long recordId, String json) {
        AtomicInteger index = new AtomicInteger();

        chunkDao.deleteChildren(recordId);
        insertChunkedChildren(json, recordId, index);
    }

    private void insertChunkedChildren(String json, long recordId, AtomicInteger index) {
        chunkDao.insertChildren(
                asChunkedStream(json)
                        .map(chunk ->
                                new Object[]{recordId, index.getAndIncrement(), chunk}
                        ).collect(Collectors.toList()));
    }

    private Stream<String> asChunkedStream(String data) {
        return chunker.split(data, 4000);
    }
}
