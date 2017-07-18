package com.example.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ResultSet;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.datastax.driver.core.schemabuilder.SchemaBuilder.udtLiteral;
import static com.sun.org.apache.xml.internal.utils.LocaleUtility.EMPTY_STRING;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.ReflectionUtils.doWithFields;

@Service
public class CassandraService {

    // TODO: 14/07/2017
    public static final String ID_NAME = "id";

    private Session session;
    private Cluster cluster;
    private Collection<UserType> types;
    private final String host;
    private final String keySpace;

    CassandraService(@Value("localhost") String host, @Value("demo") String keySpace) {
        this.host = host;
        this.keySpace = keySpace;
    }

    @PostConstruct
    void createSession() {
        try {
            cluster = Cluster.builder().addContactPoint(host).build();
            // already created
            KeyspaceMetadata space = cluster.getMetadata().getKeyspace(keySpace);
            types = space.getUserTypes();
            session = cluster.connect(keySpace);
        } catch (Exception e) {
            // TODO: 17/07/2017
        }
        // TODO: 17/07/2017 single time in keyspace
        if (isEmpty(types) && session != null){
            createType();
        }
    }

    Session getSession() {
        if (session != null) return session;
        else throw new IllegalStateException("Cassandra session not created");
    }

    public Session connect() {
        return getSession();
    }

    public void createTableByTemplate(String name, Class<?> aClass) {
        FieldCollector fc = new FieldCollector();
        doWithFields(aClass, fc);

        createTableByTemplate(name, fc);
    }

    public void createTableByTemplate(String name, Map<String, ?> map) {
        Map<String, Class> classMap = map.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getClass()));
        FieldCollector fc = new FieldCollector();
        fc.doWith(classMap);

        createTableByTemplate(name, fc);
    }

    public void createTableByTemplate(String name, String id, Map<String, String> map) {
        map.put(id, EMPTY_STRING);
        createTableByTemplate(name, map);
    }

    public ResultSet execute(Statement stmt) {
        return getSession().execute(stmt);
    }

    public void dropTable(String name) {
        getSession().execute(SchemaBuilder.dropTable(name));
    }

    private void createTableByTemplate(String name, FieldCollector fc) {
        Create createStmt = SchemaBuilder.createTable(name);
        Consumer<? super Map.Entry<String, Class>> addColumn = entry -> createColumn(entry, createStmt);

        fc.getFields().entrySet().forEach(addColumn);
        execute(createStmt);
    }

    public void createTableOfMap(String name) {
        Create createStmt = SchemaBuilder.createTable(name);
        createStmt.addPartitionKey(ID_NAME, DataType.text());
        createStmt.addUDTColumn(name, udtLiteral("map<text,text>"));
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