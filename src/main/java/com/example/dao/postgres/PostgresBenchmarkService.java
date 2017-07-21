package com.example.dao.postgres;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class PostgresBenchmarkService {

    private final PostgresDao postgresDao;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private long getRandomLong() {
        return random.nextLong(Long.MAX_VALUE);
    }

    @Autowired
    public PostgresBenchmarkService(PostgresDao postgresDao) {
        this.postgresDao = postgresDao;
    }

    public Long createKeyValueOperation(Map<String, String> map) {
        Long parentId = postgresDao.insertKeyValueMainRecord("Any operation name", "any operation type", getRandomLong());

        postgresDao.insertKeyValueChildRecord(parentId, map);

        return parentId;
    }

    public long createChunkedOperation(String data) {

        long recordId = postgresDao.insertChunkMainRecord("SomeSystem", "Some operation", getRandomLong());

        Stream<String> chunkStream = StreamSupport.stream(
                Splitter.fixedLength(4000).split(data).spliterator(), false);

        AtomicInteger index = new AtomicInteger();

        postgresDao.insertChunkChildRecord(
                chunkStream.map(chunk ->
                        new Object[]{recordId, index.getAndIncrement(), chunk}
                ).collect(Collectors.toList()));
        return recordId;
    }

    public Map<String, String> readKeyValueOperation(long operationId) {
        return postgresDao.readKeyValue(operationId);
    }

    public String readChunkOperation(long operationId) {
        return postgresDao.readChunk(operationId);
    }

    public List<Long> loadKeyValueOperationIds(int fieldsCount) {
        return postgresDao.loadKeyValueOperationIds(fieldsCount);
    }

    public List<Long> loadChunkOperationIds(int chunksCount) {
        return postgresDao.loadChunkOperationIds(chunksCount);
    }

    public Long writeJsonb(String jsonText) {
        return postgresDao.insertJson("SomeSystem", "Some operation", getRandomLong(), jsonText);
    }
}
