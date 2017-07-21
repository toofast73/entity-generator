package com.example.dao.postgres;

import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class PostgresDao {

    private static final String INSERT_KEY_VALUE_MAIN_RECORD_SQL = "INSERT INTO KEY_VAL_MAIN(edit_date, as_name, operation_type, operation_id) VALUES (?, ?, ?, ?)";
    private static final String INSERT_KEY_VALUE_CHILD_RECORD_SQL = "INSERT INTO KEY_VAL_CHILD(main_id, key, value) VALUES (?, ?, ?)";

    private static final String INSERT_CHUNK_MAIN_RECORD_SQL = "INSERT INTO chunk_main(edit_date, as_name, operation_type, operation_id) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CHUNK_CHILD_RECORD_SQL = "INSERT INTO chunk_child(main_id, chunk_num, chunk_data) VALUES (?, ?, ?)";

    private static final String SQL_READ_KEY_VAL = "SELECT key, value FROM key_val_child WHERE main_id =?";

    private static final String SQL_READ_CHUNK_VAL = "SELECT main_id, chunk_num, chunk_data FROM chunk_child WHERE main_id =?";

    private static final String SELECT_KEY_VALUE_IDS = "SELECT main_id FROM key_val_child group by 1 having count(main_id) = ?";

    private static final String SELECT_CHUNK_IDS = "SELECT main_id FROM chunk_child group by 1 having count(main_id) = ?";

    private static final String INSERT_JSON_RECORD_SQL = "INSERT INTO JSON_TABLE(edit_date, as_name, operation_type, operation_id, data_json) VALUES (?, ?, ?, ?, ?)";

    private final JdbcTemplate template;

    @Autowired
    public PostgresDao(JdbcTemplate template) {
        this.template = template;
    }

    public Long insertKeyValueMainRecord(String operation_name, String operation_type, long operationId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
                    PreparedStatement ps = con.prepareStatement(INSERT_KEY_VALUE_MAIN_RECORD_SQL, new String[]{"id"});

                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setString(2, operation_name);
                    ps.setString(3, operation_type);
                    ps.setLong(4, operationId);

                    return ps;
                },
                keyHolder
        );
        return keyHolder.getKey().longValue();
    }

    public void insertKeyValueChildRecord(Long parentId, Map<String, String> map) {
        template.batchUpdate(INSERT_KEY_VALUE_CHILD_RECORD_SQL, map.entrySet()
                        .stream()
                        .map(entry ->
                                new Object[]{parentId, entry.getKey(), entry.getValue()}
                        )
                        .collect(Collectors.toList()),
                new int[]{Types.NUMERIC, Types.VARCHAR, Types.VARCHAR});
    }

    public long insertChunkMainRecord(String operation_name, String operation_type, long operationId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
                    PreparedStatement ps = con.prepareStatement(INSERT_CHUNK_MAIN_RECORD_SQL, new String[]{"id"});

                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setString(2, operation_name);
                    ps.setString(3, operation_type);
                    ps.setLong(4, operationId);

                    return ps;
                },
                keyHolder
        );
        return keyHolder.getKey().longValue();
    }

    public void insertChunkChildRecord(List<Object[]> keyValues) {
        template.batchUpdate(INSERT_CHUNK_CHILD_RECORD_SQL, keyValues, new int[]{Types.NUMERIC, Types.NUMERIC, Types.VARCHAR});
    }

    public Map<String, String> readKeyValue(Long operationId) {

        Map<String, String> keyValues = new HashMap<>();

        template.query(SQL_READ_KEY_VAL, new Object[]{operationId},
                (RowMapper<Void>) (rs, rowNum) -> {
                    keyValues.put(rs.getString(1), rs.getString(2));
                    return null;
                });
        return keyValues;
    }

    public String readChunk(Long operationId) {

        StringBuilder data = new StringBuilder();

        template.query(SQL_READ_CHUNK_VAL, new Object[]{operationId},
                (RowMapper<Void>) (rs, rowNum) -> {
                    data.append(rs.getString(1));
                    return null;
                });

        return data.toString();
    }

    public List<Long> loadKeyValueOperationIds(int fieldsCount) {
        return template.queryForList(SELECT_KEY_VALUE_IDS, new Object[]{fieldsCount}, Long.class);
    }

    public List<Long> loadChunkOperationIds(int chunksCount) {
        return template.queryForList(SELECT_CHUNK_IDS, new Object[]{chunksCount}, Long.class);
    }

    public Long insertJson(String operation_name, String operation_type, Long operationId, String jsonText) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
                    PreparedStatement ps = con.prepareStatement(INSERT_JSON_RECORD_SQL, new String[]{"id"});

                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setString(2, operation_name);
                    ps.setString(3, operation_type);
                    ps.setLong(4, operationId);

                    PGobject json = new PGobject();
                    json.setType("json");
                    json.setValue(jsonText);
                    ps.setObject(5, json);

                    return ps;
                },
                keyHolder
        );
        return keyHolder.getKey().longValue();
    }
}
