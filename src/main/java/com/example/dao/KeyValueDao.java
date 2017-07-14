package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
class KeyValueDao {

    private static final String SQL_INSERT_MAIN_KEY_VAL =
            "INSERT INTO KEY_VAL_MAIN VALUES (" +
                    "SEQ_KEY_VAL_MAIN.nextval, ?, ?, ?, ?)";

    private static final String SQL_INSERT_CHILD_KEY_VAL =
            "INSERT INTO KEY_VAL_CHILD VALUES (?, ?, ?)";

    private static final String SQL_READ_KEY_VAL =
            "SELECT KEY, VALUE FROM KEY_VAL_CHILD" +
                    " WHERE MAIN_ID = ?";

    private final JdbcTemplate jt;

    @Autowired
    public KeyValueDao(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    Long insertMain(long operationId, String systemName, String operationType) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jt.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SQL_INSERT_MAIN_KEY_VAL, new String[]{"ID"});

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
        jt.batchUpdate(SQL_INSERT_CHILD_KEY_VAL, keyValues);
    }

    Map<String, String> read(Long operationId) {

        Map<String, String> keyValues = new HashMap<>();

        jt.query(SQL_READ_KEY_VAL, new Object[]{operationId},
                (RowMapper<Void>) (rs, rowNum) -> {
                    keyValues.put(rs.getString(1), rs.getString(2));
                    return null;
                });
        return keyValues;
    }
}
