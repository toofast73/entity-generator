package com.example.data.converter;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JacksonMarshallerTest {

    String JSON_STRING = "{\"name\":\"example\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
    String MAP_TO_STRING = "{name=example, age=33, position=Developer, salary=7500, skills=[java, python]}";

    private JacksonMarshaller jacksonMarshaller;
    private Staff staff;

    @Before
    public void setUp() throws Exception {
        staff = Staff.createDummyObject();
        jacksonMarshaller = new JacksonMarshaller();
    }

    @Test
    public void toJson() throws Exception {
        assertEquals(jacksonMarshaller.toJson(staff), JSON_STRING);
    }

    @Test
    public void toMap() throws Exception {
        assertEquals(jacksonMarshaller.toMap(staff).toString(), MAP_TO_STRING);
    }

    @Test
    public void fromJson() throws Exception {

        assertEquals(jacksonMarshaller.fromJson(JSON_STRING, Staff.class), staff);
    }

    @Test
    public void fromMap() throws Exception {
        Map<Object, Object> map = jacksonMarshaller.toMap(staff);
        assertEquals(jacksonMarshaller.fromMap(map), JSON_STRING);
    }

    @Test
    public void map2Object() throws Exception {
        Map<Object, Object> map = jacksonMarshaller.toMap(staff);
        assertTrue(jacksonMarshaller.map2Object(map, Staff.class).equals(staff));
    }
}