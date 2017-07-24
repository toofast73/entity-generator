package com.example.benchmark.cassandra;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import com.example.benchmark.Util;
import com.example.dao.IdGenerator;
import com.example.dao.cassandra.CassandraDao;
import com.example.dao.cassandra.CassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;
import static com.example.dao.cassandra.CassandraService.ID_NAME;
import static java.util.stream.Collectors.toMap;

@Service
public class CassandraBenchmarkService {
    private static final String BENCHMARK_TABLE = "benchmark";
    private static final String EMPTY_STRING = "";

    @Autowired
    private CassandraService cassandraService;
    @Autowired
    private CassandraDao cassandraDao;
    @Autowired
    private IdGenerator idGenerator;

    public void create() {
        cassandraService.createTableOfMap(BENCHMARK_TABLE);
    }

    public void create(Map<String, String> map) {
        cassandraService.createTableByTemplate(BENCHMARK_TABLE, map);
        ;
    }

    public long writeMapAsMap(Map<String, String> operationData) {
        long id = idGenerator.generateId();
        cassandraDao.insertMapAsMap(BENCHMARK_TABLE, String.valueOf(id), operationData);
        return id;
    }

    public long writeMapAsKeyValue(Map<String, String> operationData) {
        long id = idGenerator.generateId();
        cassandraDao.insertMapAsKeyValue(BENCHMARK_TABLE, String.valueOf(id), operationData);
        return id;
    }

    public Object writeJson(String json) {
        long id = idGenerator.generateId();
        cassandraDao.insertJson(BENCHMARK_TABLE, String.valueOf(id), json);
        return id;
    }

    public void drop() {
        cassandraService.dropTable(BENCHMARK_TABLE);
    }

    public Map<String, String> readMap(Long operationId) {
        return readMap(operationId.toString());
    }

    //todo extract to CassandraDao
    public Map<String, String> readMap(String operationId) {
        Select.Where select = QueryBuilder.select().from(BENCHMARK_TABLE).where(eq(ID_NAME, operationId));
        Row one = cassandraService.execute(select).one();
        return one == null ? null : one.getMap(BENCHMARK_TABLE, String.class, String.class);
    }

    //todo extract to CassandraDao
    public Map<String, String> read(String operationId, Map<String, String> pattern) {
        Select.Where where = QueryBuilder.select().from(BENCHMARK_TABLE).where(eq(ID_NAME, operationId));
        Row one = cassandraService.execute(where).one();
        return one == null ? null : pattern.keySet().stream().collect(toMap(key -> key, key -> one.get(key, String.class)));
    }

    //todo extract to CassandraDao
    public void editKeyValue(String id, Util.KeyValueEditInfo editInfo) {
        final Assign assign = new Assign();

        Update update = QueryBuilder.update(BENCHMARK_TABLE);
        editInfo.keysToDelete.forEach((key, value) -> assign.assign = update.with(set(key, EMPTY_STRING)));
        editInfo.keysToInsert.forEach((key, value) -> assign.assign = update.with(set(key, value)));
        editInfo.keysToUpdate.forEach((key, value) -> assign.assign = update.with(set(key, value)));

        if (assign.assign == null) return;
        Update.Where where = assign.assign.where(eq(ID_NAME, id));
        cassandraService.execute(where);
    }

    class Assign {
        Update.Assignments assign;
    }
}
