package com.example.dao.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final String SELECT_KEY_VALUE_OPERATION_IDS =
            "SELECT ID FROM KEY_VAL_MAIN m" +
                    " WHERE ROWNUM <= 10" +
                    "      AND ? = (SELECT count(1) FROM KEY_VAL_CHILD c WHERE c.MAIN_ID = m.ID)";

    private static final String SQL_UPDATE_CHILD_KEY_VAL =
            "UPDATE KEY_VAL_CHILD SET VALUE = ? WHERE MAIN_ID = ? AND KEY = ?";

    private static final String SQL_DELETE_CHILD_KEY =
            "DELETE KEY_VAL_CHILD WHERE MAIN_ID = ? AND KEY = ?";

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

    void insertChildren(long recordId, Map<String, String> keyValues) {
        jt.batchUpdate(SQL_INSERT_CHILD_KEY_VAL, keyValues.entrySet()
                        .stream()
                        .map(entry ->
                                new Object[]{recordId, entry.getKey(), entry.getValue()}
                        )
                        .collect(Collectors.toList()),
                new int[]{Types.NUMERIC, Types.VARCHAR, Types.VARCHAR});
    }

    List<Long> loadKeyValueOperationIds(int fieldsCount) {
        return jt.queryForList(SELECT_KEY_VALUE_OPERATION_IDS, new Object[]{fieldsCount}, Long.class);
    }

    void deleteChildren(int recordId, Map<String, String> keyValues) {
        jt.batchUpdate(SQL_DELETE_CHILD_KEY, keyValues.entrySet()
                        .stream()
                        .map(entry ->
                                new Object[]{recordId, entry.getKey()}
                        )
                        .collect(Collectors.toList()),
                new int[]{Types.NUMERIC, Types.VARCHAR});
    }

    void updateChildren(int recordId, Map<String, String> keyValues) {
        jt.batchUpdate(SQL_UPDATE_CHILD_KEY_VAL, keyValues.entrySet()
                        .stream()
                        .map(entry ->
                                new Object[]{entry.getValue(), recordId, entry.getKey()}
                        )
                        .collect(Collectors.toList()),
                new int[]{Types.VARCHAR, Types.NUMERIC, Types.VARCHAR});
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
