package com.example.dao.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class ReaderService {

    @Autowired
    private KeyValueDao keyValueDao;
    @Autowired
    private ChunkDao chunkDao;

    public Map<String, String> readKeyValueOperation(Long operationId) {
        return keyValueDao.read(operationId);
    }

    public String readChunkOperation(Long operationId) {
        return chunkDao.read(operationId);
    }

    public List<Long> loadKeyValueOperationIds(int fieldsCount) {
        return keyValueDao.loadKeyValueOperationIds(fieldsCount);
    }

    public List<Long> loadChunkOperationIds(int fieldsCount) {
        return chunkDao.loadKeyValueOperationIds(fieldsCount);
    }
}
