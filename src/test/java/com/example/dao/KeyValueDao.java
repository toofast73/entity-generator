package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
class KeyValueDao {

    private final JdbcTemplate jt;

    @Autowired
    public KeyValueDao(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }
}
