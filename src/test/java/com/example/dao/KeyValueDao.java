package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
                    "SEQ_KEY_VAL_MAIN.nextval, ?, ?, ?, ?" +
                    ")";

    private static final String SQL_INSERT_CHILD_KEY_VAL =
            "INSERT INTO KEY_VAL_CHILD VALUES (?, ?, ?)";

    private static final String SQL_READ_KEY_VAL =
            "SELECT KEY, VALUE FROM KEY_VAL_CHILD" +
                    " WHERE MAIN_ID = (SELECT ID FROM KEY_VAL_MAIN WHERE OPERATION_ID = ?)" +
                    "";

    private final JdbcTemplate jt;

    @Autowired
    public KeyValueDao(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    void insertMain(long operationId, String systemName, String operationType) {
        jt.update(SQL_INSERT_MAIN_KEY_VAL, new Date(), systemName, operationType, operationId);
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
