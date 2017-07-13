package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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
}
