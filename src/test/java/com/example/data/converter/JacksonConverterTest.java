package com.example.data.converter;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class JacksonConverterTest {

    String JSON_STRING = "{\"name\":\"example\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
    String MAP_TO_STRING = "{name=example, age=33, position=Developer, salary=7500, skills=[java, python]}";

    private JacksonConverter jacksonConverter;
    private Staff staff;

    @Before
    public void setUp() throws Exception {
        staff = Staff.createDummyObject();
        jacksonConverter = new JacksonConverter();
    }

    @Test
    public void toJson() throws Exception {
        assertTrue(jacksonConverter.toJson(staff).equals(JSON_STRING));
    }

    @Test
    public void toMap() throws Exception {
        assertTrue(jacksonConverter.toMap(staff).toString().equals(MAP_TO_STRING));
    }

    @Test
    public void fromJson() throws Exception {

        assertTrue(jacksonConverter.fromJson(JSON_STRING, Staff.class).equals(staff));
    }

    @Test
    public void fromMap() throws Exception {
        Map<Object, Object> map = jacksonConverter.toMap(staff);
        assertTrue(jacksonConverter.fromMap(map).equals(JSON_STRING));
    }

    @Test
    public void map2Object() throws Exception {
        Map<Object, Object> map = jacksonConverter.toMap(staff);
        assertTrue(jacksonConverter.map2Object(map, Staff.class).equals(staff));
    }
}