package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String readChunkedOperation(Long operationId) {
        return chunkDao.read(operationId);
    }
}
