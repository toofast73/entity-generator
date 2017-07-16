package com.example.cassandra;

import com.datastax.driver.core.Session;
import com.example.Start;
import com.example.converter.JsonToKeyValueConverter;
import com.example.mapper.JacksonMapper;
import com.example.mapper.Staff;
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
    private JacksonMapper jacksonMapper;
    @Autowired
    private CassandraDao cassandraDao;
    @Autowired
    private JsonToKeyValueConverter jsonToKeyValueConverter;

    private Session session;

    @Before
    public void setUp() throws Exception {
        session = cassandraService.connect();
    }

    @Test
    public void connect() throws Exception {
        assertNotNull(session);
    }

    @Test
    public void testInsertJsonIntoTable() throws Exception {
        String name = Staff.class.getSimpleName().toLowerCase();
        Staff staff = Staff.createDummyObject();
        String jsonStaff = jacksonMapper.toJson(staff);
        // createTable
        cassandraService.createTableByTemplate(name, Staff.class);
        // insert
        cassandraDao.insertJson(name, jsonStaff);
        // drop
        cassandraService.dropTable(name);
    }

    @Test
    public void testInsertMapIntoTable() throws Exception {
        String name = Staff.class.getSimpleName().toLowerCase();
        Staff staff = Staff.createDummyObject();
        String jsonStaff = jacksonMapper.toJson(staff);

        jsonToKeyValueConverter.cql();
        Map<String, String> map = jsonToKeyValueConverter.convert(jsonStaff);
        // createTable
        cassandraService.createTableByTemplate(name, map);
        // insert
        cassandraDao.insertMap(name, map);
        // drop
        cassandraService.dropTable(name);
    }
}