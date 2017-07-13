package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


public class JsonGeneratorApplicationTests {

	@Test
	public void generatePerson() {

		PersonGenerator personGenerator = new PersonGenerator();
		Person generate = personGenerator.generate(10000, 100);

		System.out.println(generate);
	}

}
