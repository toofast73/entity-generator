package com.example.demo;

import com.example.data.converter.JacksonMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;


public class JsonGeneratorApplicationTests {

	@Test
	public void generatePerson() throws JsonProcessingException {

		PersonGenerator personGenerator = new PersonGenerator();
		Person generate = personGenerator.generate(10000, 100);

		System.out.println(generate);

		JacksonMarshaller jacksonMarshaller = new JacksonMarshaller();
		String json = jacksonMarshaller.toJson(generate);
		System.out.println(json);
	}

}
