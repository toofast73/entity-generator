package com.example.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.CreateType;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.example.mapper.CustomTypes.Department;
import com.example.mapper.FieldCollector;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.datastax.driver.core.schemabuilder.SchemaBuilder.udtLiteral;
import static org.springframework.util.ReflectionUtils.doWithFields;

@Service
public class CassandraService {

    // TODO: 14/07/2017
    public static final String ID_NAME = "id";

    private final Session session;
    private final Cluster cluster;
    private final Collection<UserType> types;

    CassandraService(@Value("localhost") String host, @Value("demo") String keySpace) {
        cluster = Cluster.builder().addContactPoint(host).build();
        // already created
        KeyspaceMetadata space = cluster.getMetadata().getKeyspace(keySpace);
        types = space.getUserTypes();
        session = cluster.connect(keySpace);
        //        createType(); single time in keyspace
    }

    public Session connect() {
        return session;
    }

    public void createTableByTemplate(String name, Class<?> aClass) throws JsonProcessingException {
        Create createStmt = SchemaBuilder.createTable(name);
        Consumer<? super Map.Entry<String, Class>> addColumn = entry -> createColumn(entry, createStmt);

        FieldCollector fc = new FieldCollector();
        doWithFields(aClass, fc);
        fc.getFields().entrySet().stream().forEach(addColumn);
        execute(createStmt);
    }

    public void execute(Statement stmt) {
        session.execute(stmt);
    }

    public void createTableByTemplate(Class<?> aClass) throws JsonProcessingException {
        createTableByTemplate(aClass.getSimpleName(), aClass);
    }

    public void dropTable(String name) {
        session.execute(SchemaBuilder.dropTable(name));
    }

    public void dropTable(Class<?> aClass) {
        session.execute(SchemaBuilder.dropTable(aClass.getSimpleName()));
    }

    private void createColumn(Map.Entry<String, Class> entry, Create createStmt) {

        if (entry.getKey().equals(ID_NAME)) {
            createStmt.addPartitionKey(ID_NAME, DataType.text());
            return;
        }

        Class value = entry.getValue();
        // TODO: 14/07/2017 mapper
        if (value.isAssignableFrom(List.class)) {
            createStmt.addColumn(entry.getKey(), DataType.list(DataType.text()));
        } else if (value.isAssignableFrom(Integer.class)) {
            createStmt.addColumn(entry.getKey(), DataType.varint());
        } else if (value.isAssignableFrom(BigDecimal.class)) {
            createStmt.addColumn(entry.getKey(), DataType.decimal());
        } else if (value.isAssignableFrom(Department.class)) { // TODO: 14/07/2017
            createStmt.addUDTColumn(entry.getKey(), udtLiteral(Department.class.getSimpleName().toLowerCase()));
        } else {
            createStmt.addColumn(entry.getKey(), DataType.text());//?
        }
    }

    // TODO: 14/07/2017
    private void createType() {
        CreateType createType = SchemaBuilder.createType("department");
        createType.addColumn("name", DataType.text());
        createType.addColumn("boss", DataType.text());
        execute(createType);
    }

}