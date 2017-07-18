package com.example.demo;

import com.example.data.converter.JacksonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;


public class JsonGeneratorApplicationTests {

	@Test
	public void generatePerson() throws JsonProcessingException {

		PersonGenerator personGenerator = new PersonGenerator();
		Person generate = personGenerator.generate(10000, 100);

		System.out.println(generate);

		JacksonConverter jacksonConverter = new JacksonConverter();
		String json = jacksonConverter.toJson(generate);
		System.out.println(json);
	}

}
