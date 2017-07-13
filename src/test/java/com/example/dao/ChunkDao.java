package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Repository
class ChunkDao {

    private static final String SQL_INSERT_MAIN_CHUNK =
            "INSERT INTO CHUNK_MAIN VALUES (" +
                    "SEQ_CHUNK_MAIN.nextval, ?, ?, ?, ?)";

    private static final String SQL_INSERT_CHILD_CHUNK =
            "INSERT INTO CHUNK_CHILD VALUES (?, ?, ?)";

    private static final String SQL_READ_CHUNKS =
            "SELECT CHUNK_DATA FROM CHUNK_CHILD" +
                    " WHERE MAIN_ID = (SELECT ID FROM CHUNK_MAIN WHERE OPERATION_ID = ?)" +
                    " ORDER BY CHUNK_NUM";

    private final JdbcTemplate jt;

    @Autowired
    public ChunkDao(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    void insertMain(long operationId, String systemName, String operationType) {
        jt.update(SQL_INSERT_MAIN_CHUNK, new Date(), systemName, operationType, operationId);
    }

    void insertChildren(List<Object[]> keyValues) {
        jt.batchUpdate(SQL_INSERT_CHILD_CHUNK, keyValues);
    }

    String read(Long operationId) {

        StringBuilder data = new StringBuilder();

        jt.query(SQL_READ_CHUNKS, new Object[]{operationId},
                (RowMapper<Void>) (rs, rowNum) -> {
                    data.append(rs.getString(1));
                    return null;
                });

        return data.toString();
    }
}
