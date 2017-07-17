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
import com.datastax.driver.core.schemabuilder.UDTType;
import com.example.mapper.CustomTypes.Department;
import com.example.mapper.FieldCollector;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.datastax.driver.core.schemabuilder.SchemaBuilder.udtLiteral;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ReflectionUtils.doWithFields;

//@Service
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
//        createType(); //single time in keyspace
    }

    public Session connect() {
        return session;
    }

    public void createTableByTemplate(String name, Class<?> aClass) {
        FieldCollector fc = new FieldCollector();
        doWithFields(aClass, fc);

        createTableByTemplate(name, fc);
    }

    public void createTableByTemplate(String name, Map<String, ?> objectMap) {
        Map<String, Class> classMap = objectMap.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getClass()));
        FieldCollector fc = new FieldCollector();
        fc.doWith(classMap);

        createTableByTemplate(name, fc);
    }

    public void execute(Statement stmt) {
        session.execute(stmt);
    }

    public void dropTable(String name) {
        session.execute(SchemaBuilder.dropTable(name));
    }

    private void createTableByTemplate(String name, FieldCollector fc) {
        Create createStmt = SchemaBuilder.createTable(name);
        Consumer<? super Map.Entry<String, Class>> addColumn = entry -> createColumn(entry, createStmt);

        fc.getFields().entrySet().forEach(addColumn);
        execute(createStmt);
    }

    private void createColumn(Map.Entry<String, Class> entry, Create createStmt) {

        if (entry.getKey().equals(ID_NAME)) {
            createStmt.addPartitionKey(ID_NAME, DataType.text());
            return;
        }
        Class value = entry.getValue();
        DataType dataType = mapCQLType(value);
        if (dataType != null) {
            createStmt.addColumn(entry.getKey(), dataType);
        } else {
            UDTType udtType = mapUserType(value);
            notNull(udtType, "Couldn't map type " + value);
            createStmt.addUDTColumn(entry.getKey(), udtType);
        }
    }

    // TODO: 14/07/2017 mapper
    private UDTType mapUserType(Class value) {

        if (value.isAssignableFrom(Department.class)) {
            return udtLiteral(Department.class.getSimpleName().toLowerCase());
        } else {
            return null;
        }
    }

    // TODO: 14/07/2017 mapper
    private DataType mapCQLType(Class value) {
        if (value.isAssignableFrom(String.class)) {
            return DataType.text();
        } else if (value.isAssignableFrom(Integer.class)) {
            return DataType.varint();
        } else if (value.isAssignableFrom(BigDecimal.class)) {
            return DataType.decimal();
        } else if (value.isAssignableFrom(List.class)) {
            return DataType.list(DataType.text());//
        } else {
            return null;
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