package com.example.converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class JacksonMapperTest {

    String toJson  = "{\"name\":\"example\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
    String toMap = "{name=example, age=33, position=Developer, salary=7500, skills=[java, python]}";
    String fromMap = "{\"name\":\"example\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";


    private JacksonMapper jacksonMapper;
    private Staff staff;

    static Staff createDummyObject() {
        Staff staff = new Staff();
        staff.setName("example");
        staff.setAge(33);
        staff.setPosition("Developer");
        staff.setSalary(new BigDecimal("7500"));
        List<String> skills = new ArrayList<>();
        skills.add("java");
        skills.add("python");
        staff.setSkills(skills);
        return staff;
    }


    @Before
    public void setUp() throws Exception {
        staff = createDummyObject();
        jacksonMapper = new JacksonMapper();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void toJson() throws Exception {
        assertTrue(jacksonMapper.toJson(staff).equals(toJson));
    }

    @Test
    public void toMap() throws Exception {
        assertTrue(jacksonMapper.toMap(staff).toString().equals(toMap));
    }

    @Test
    public void fromJson() throws Exception {

        assertTrue(jacksonMapper.fromJson(toJson, Staff.class).equals(staff));
    }

    @Test
    public void fromMap() throws Exception {
        Map<Object, Object> map = jacksonMapper.toMap(staff);
        assertTrue(jacksonMapper.fromMap(map).equals(fromMap));
    }

    @Test
    public void map2Object() throws Exception {
        Map<Object, Object> map = jacksonMapper.toMap(staff);
        assertTrue(jacksonMapper.map2Object(map, Staff.class).equals(staff));
    }
}