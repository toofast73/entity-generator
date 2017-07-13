package com.example.dao;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        keyValueDao.insertChildren(
                data.entrySet()
                        .stream()
                        .map(entry ->
                                new Object[]{operationId, entry.getKey(), entry.getValue()}
                        )
                        .collect(Collectors.toList()));
    }

    public void storeChunks(String data) {

        long operationId = idGenerator.generateId();

        chunkDao.insertMain(operationId, "SomeSystem", "Some operation");
        List<String> chunks = Splitter.fixedLength(4000).splitToList(data);

        List<Object[]> params = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            params.add(new Object[]{operationId, i, chunks.get(i)});
        }
        chunkDao.insertChildren(params);
    }
}
