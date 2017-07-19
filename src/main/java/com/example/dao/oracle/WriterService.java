package com.example.dao.oracle;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public long createKeyValueOperation(Map<String, String> data) {

        long recordId = keyValueDao.insertMain(
                idGenerator.generateId(), "SomeSystem", "Some operation");
        keyValueDao.insertChildren(
                data.entrySet()
                        .stream()
                        .map(entry ->
                                new Object[]{recordId, entry.getKey(), entry.getValue()}
                        )
                        .collect(Collectors.toList()));
        return recordId;
    }

    public long createChunkedOperation(String data) {

        long recordId = chunkDao.insertMain(
                idGenerator.generateId(), "SomeSystem", "Some operation");

        Stream<String> chunkStream = StreamSupport.stream(
                Splitter.fixedLength(4000).split(data).spliterator(), false);

        AtomicInteger index = new AtomicInteger();

        chunkDao.insertChildren(
                chunkStream.map(chunk ->
                        new Object[]{recordId, index.getAndIncrement(), chunk}
                ).collect(Collectors.toList()));
        return recordId;
    }
}
