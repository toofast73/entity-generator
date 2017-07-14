package com.example.mapper;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class JacksonMapperTest {

    String JSON_STRING = "{\"name\":\"example\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
    String MAP_TO_STRING = "{name=example, age=33, position=Developer, salary=7500, skills=[java, python]}";

    private JacksonMapper jacksonMapper;
    private Staff staff;

    @Before
    public void setUp() throws Exception {
        staff = Staff.createDummyObject();
        jacksonMapper = new JacksonMapper();
    }

    @Test
    public void toJson() throws Exception {
        assertTrue(jacksonMapper.toJson(staff).equals(JSON_STRING));
    }

    @Test
    public void toMap() throws Exception {
        assertTrue(jacksonMapper.toMap(staff).toString().equals(MAP_TO_STRING));
    }

    @Test
    public void fromJson() throws Exception {

        assertTrue(jacksonMapper.fromJson(JSON_STRING, Staff.class).equals(staff));
    }

    @Test
    public void fromMap() throws Exception {
        Map<Object, Object> map = jacksonMapper.toMap(staff);
        assertTrue(jacksonMapper.fromMap(map).equals(JSON_STRING));
    }

    @Test
    public void map2Object() throws Exception {
        Map<Object, Object> map = jacksonMapper.toMap(staff);
        assertTrue(jacksonMapper.map2Object(map, Staff.class).equals(staff));
    }
}