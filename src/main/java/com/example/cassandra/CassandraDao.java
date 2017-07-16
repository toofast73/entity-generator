package com.example.cassandra;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CassandraDao {

    @Autowired
    private CassandraService cassandraService;

    public void insertJson(String name, String entity) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        insertStmt.json(entity);
        cassandraService.execute(insertStmt);
    }

    public void insertMap(String name, Map<String, ?> map) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        map.entrySet().forEach(entry -> insertStmt.value(entry.getKey(), entry.getValue()));
        cassandraService.execute(insertStmt);
    }
}
