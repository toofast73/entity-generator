package com.example.cassandra;

import com.datastax.driver.core.Session;
import com.example.Start;
import com.example.mapper.JacksonMapper;
import com.example.mapper.Staff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void testTable() throws Exception {
        Staff staff = Staff.createDummyObject();

        String jsonStaff = jacksonMapper.toJson(staff);

        cassandraService.createTableByTemplate(Staff.class);

        cassandraDao.insertJson(Staff.class.getSimpleName(), jsonStaff);

        cassandraService.dropTable(Staff.class);
    }
}