package com.example.dao.cassandra;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.dao.cassandra.CassandraService.ID_NAME;

@Service
public class CassandraDao {

    @Autowired
    private CassandraService cassandraService;

    public void insertJson(String name, String entity) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        insertStmt.json(entity);
        cassandraService.execute(insertStmt);
    }

    public void insertJson(String name, String id, String entity) {
        Insert insertStmt = QueryBuilder.insertInto(name);
//        insertStmt.value(ID_NAME, id);
        insertStmt.json(entity);
        cassandraService.execute(insertStmt);
    }

    public void insertMapAsKeyValue(String name, Map<String, ?> map) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        map.entrySet().forEach(entry -> insertStmt.value(entry.getKey(), entry.getValue()));
        cassandraService.execute(insertStmt);
    }

    public void insertMapAsMap(String name, Map<String, String> map) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        insertStmt.value(ID_NAME, map.get(ID_NAME));
        insertStmt.value(name, map);
        cassandraService.execute(insertStmt);
    }

    public void insertMapAsMap(String name, String id, Map<String, String> map) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        insertStmt.value(ID_NAME, id);
        insertStmt.value(name, map);
        cassandraService.execute(insertStmt);
    }

    public void insertMapAsKeyValue(String name, String id, Map<String, String> map) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        insertStmt.value(ID_NAME, id);
        map.entrySet().forEach(entry -> insertStmt.value(entry.getKey(), entry.getValue()));
        cassandraService.execute(insertStmt);
    }

}
