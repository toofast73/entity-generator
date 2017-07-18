package com.example.cassandra;

import com.datastax.driver.core.Session;
import com.example.Start;
import com.example.data.converter.JacksonConverter;
import com.example.data.converter.JsonToKeyValueConverter;
import com.example.data.converter.Staff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
public class CassandraServiceTest {

    @Autowired
    private CassandraService cassandraService;
    @Autowired
    private JacksonConverter jacksonConverter;
    @Autowired
    private CassandraDao cassandraDao;
    @Autowired
    private JsonToKeyValueConverter jsonToKeyValueConverter;

     private Session session;
    private String name;
    private Staff staff;
    private String jsonStaff;
    private Map<String, String> mapStaff;

    @Before
    public void setUp() throws Exception {
        session = cassandraService.connect();
        name = Staff.class.getSimpleName().toLowerCase();
        staff = Staff.createDummyObject();
        jsonStaff = jacksonConverter.toJson(staff);
        jsonToKeyValueConverter.cqlMode();
        mapStaff = jsonToKeyValueConverter.convert(jsonStaff);

    }

    @Test
    public void connect() throws Exception {
        assertNotNull(session);
    }

    @Test
    public void testInsertJsonIntoTable() throws Exception {
        // createTable
        cassandraService.createTableByTemplate(name, Staff.class);
        // insert
        cassandraDao.insertJson(name, jsonStaff);
        // drop
        cassandraService.dropTable(name);
    }

    @Test
    public void testInsertMapIntoTable() throws Exception {
        // createTable
        cassandraService.createTableByTemplate(name, mapStaff);
        // insert
        cassandraDao.insertMapIntoTable(name, mapStaff);
        // drop
        cassandraService.dropTable(name);
    }

    @Test
    public void testInsertMap() throws Exception {
        // createTable
        cassandraService.createTableOfMap(name);
        // insert
        cassandraDao.insertMapIntoMap(name, mapStaff);
        // drop
        cassandraService.dropTable(name);
    }
}