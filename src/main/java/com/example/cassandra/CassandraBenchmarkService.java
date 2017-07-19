package com.example.cassandra;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.example.dao.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.example.cassandra.CassandraService.ID_NAME;

@Service
public class CassandraBenchmarkService {
    private static final String BENCHMARK_TABLE = "benchmark";

    @Autowired
    private CassandraService cassandraService;
    @Autowired
    private CassandraDao cassandraDao;
    @Autowired
    private IdGenerator idGenerator;


    public void createBenchmarkTable(){
        cassandraService.createTableOfMap(BENCHMARK_TABLE);
    }

    public void createBenchmarkTable(Map<String, String> map){
        cassandraService.createTableByTemplate(BENCHMARK_TABLE, ID_NAME, map);
    }

    public long writeBenchmarkMapToMap(Map<String, String> operationData){
        long id = idGenerator.generateId();
        cassandraDao.insertMapIntoMap(BENCHMARK_TABLE, String.valueOf(id), operationData);
        return id;
    }

    public long writeBenchmarkMapToTable(Map<String, String> operationData){
        long id = idGenerator.generateId();
        cassandraDao.insertMapIntoTable(BENCHMARK_TABLE, String.valueOf(id), operationData);
        return id;
    }

    public void dropBenchmarkTable(){
        cassandraService.dropTable(BENCHMARK_TABLE);
    }

    public Map<String, String> readBenchmarkMapTable(Long operationId) {
        Select.Where select = QueryBuilder.select().from(BENCHMARK_TABLE).where(eq(ID_NAME, operationId.toString()));
        return cassandraService.execute(select).one().getMap(BENCHMARK_TABLE,String.class, String.class);
    }

    public Map<String, String> readBenchmarkMapTable(String operationId) {
        Select.Where select = QueryBuilder.select().from(BENCHMARK_TABLE).where(eq(ID_NAME, operationId));
        return cassandraService.execute(select).one().getMap(BENCHMARK_TABLE,String.class, String.class);
    }
}
