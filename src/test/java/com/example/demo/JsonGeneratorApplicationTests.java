package com.example.demo;

import com.example.converter.JacksonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


public class JsonGeneratorApplicationTests {

	@Test
	public void generatePerson() throws JsonProcessingException {

		PersonGenerator personGenerator = new PersonGenerator();
		Person generate = personGenerator.generate(10000, 100);

		System.out.println(generate);

		JacksonMapper jacksonMapper = new JacksonMapper();
		String json = jacksonMapper.toJson(generate);
		System.out.println(json);
	}

}
