package com.example.dao.cassandra;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.example.dao.oracle.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.example.dao.cassandra.CassandraService.ID_NAME;

@Service
public class CassandraBenchmarkService {
    private static final String BENCHMARK_TABLE = "benchmark";

    @Autowired
    private CassandraService cassandraService;
    @Autowired
    private CassandraDao cassandraDao;
    @Autowired
    private IdGenerator idGenerator;


    public void create(){
        cassandraService.createTableOfMap(BENCHMARK_TABLE);
    }

    public void create(Map<String, String> map){
        cassandraService.createTableByTemplate(BENCHMARK_TABLE, ID_NAME, map);
    }

    public long writeMapAsMap(Map<String, String> operationData){
        long id = idGenerator.generateId();
        cassandraDao.insertMapAsMap(BENCHMARK_TABLE, String.valueOf(id), operationData);
        return id;
    }

    public long writeMapAsKeyValue(Map<String, String> operationData){
        long id = idGenerator.generateId();
        cassandraDao.insertMapAsKeyValue(BENCHMARK_TABLE, String.valueOf(id), operationData);
        return id;
    }

    public Object writeJson(String json) {
        long id = idGenerator.generateId();
        cassandraDao.insertJson(BENCHMARK_TABLE, String.valueOf(id), json);
        return id;
    }

    public void drop(){
        cassandraService.dropTable(BENCHMARK_TABLE);
    }

    public Map<String, String> read(Long operationId) {
        return read(operationId.toString());
    }

    public Map<String, String> read(String operationId) {
        Select.Where select = QueryBuilder.select().from(BENCHMARK_TABLE).where(eq(ID_NAME, operationId));
        Row one = cassandraService.execute(select).one();
        return one == null? null : one.getMap(BENCHMARK_TABLE,String.class, String.class);
    }
}
