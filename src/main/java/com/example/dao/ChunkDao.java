package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
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
                    " WHERE MAIN_ID = ?" +
                    " ORDER BY CHUNK_NUM";

    private final JdbcTemplate jt;

    @Autowired
    public ChunkDao(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    Long insertMain(long operationId, String systemName, String operationType) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jt.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(SQL_INSERT_MAIN_CHUNK, new String[]{"ID"});

                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setString(2, systemName);
                    ps.setString(3, operationType);
                    ps.setLong(4, operationId);
                    return ps;
                },
                keyHolder);
        return keyHolder.getKey().longValue();
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
