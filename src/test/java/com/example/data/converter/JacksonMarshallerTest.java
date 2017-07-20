package com.example.data.converter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JacksonMarshallerTest {

    String JSON_STRING = "{\"id\":null,\"name\":\"example\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"department\":{\"name\":\"\",\"boss\":\"\"},\"skills\":[\"java\",\"python\"],\"test\":[\"+\",\"-\"]}";
    String MAP_TO_STRING = "{id=null, name=example, age=33, position=Developer, salary=7500, department={name=, boss=}, skills=[java, python], test=[+, -]}";

    private JacksonMarshaller jacksonMarshaller;
    private Staff staff;

    @Before
    public void setUp() throws Exception {
        staff = Staff.createDummyObject();
        jacksonMarshaller = new JacksonMarshaller();
    }

    @Test
    public void toJson() throws Exception {
        assertEquals(JSON_STRING, jacksonMarshaller.toJson(staff));
    }

    @Test
    public void toMap() throws Exception {
        assertEquals(MAP_TO_STRING, jacksonMarshaller.toMap(staff).toString());
    }

    @Test
    public void fromJson() throws Exception {
        assertEquals(staff, jacksonMarshaller.fromJson(JSON_STRING, Staff.class));
    }

    @Test
    public void fromMap() throws Exception {
        assertEquals(JSON_STRING, jacksonMarshaller.fromMap(
                jacksonMarshaller.toMap(staff))
        );
    }

    @Test
    public void map2Object() throws Exception {
        assertEquals(staff, jacksonMarshaller.map2Object(
                jacksonMarshaller.toMap(staff), Staff.class)
        );
    }
}