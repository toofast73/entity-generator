package com.example.dao;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    public void storeKeyValue(Map<String, String> data) {

        long operationId = idGenerator.generateId();

        keyValueDao.insertMain(operationId, "SomeSystem", "Some operation");

        for (Map.Entry<String, String> entry : data.entrySet()) {
            keyValueDao.insertChild(operationId, entry.getKey(), entry.getValue());
        }
    }

    public void storeChunks(String data) {

        long operationId = idGenerator.generateId();

        chunkDao.insertMain(operationId, "SomeSystem", "Some operation");
        List<String> chunks = Splitter.fixedLength(4000).splitToList(data);

        for (int i = 0; i < chunks.size(); i++) {
            chunkDao.insertChild(operationId, i, chunks.get(i));
        }
    }
}
