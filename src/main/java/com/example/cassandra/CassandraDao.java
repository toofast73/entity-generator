package com.example.cassandra;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CassandraDao {

    @Autowired
    private CassandraService cassandraService;

    public void insertJson(String name, String entity) {
        Insert insertStmt = QueryBuilder.insertInto(name);
        insertStmt.json(entity);
        cassandraService.execute(insertStmt);
    }
}
