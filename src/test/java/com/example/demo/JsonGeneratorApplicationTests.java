package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


public class JsonGeneratorApplicationTests {

	@Test
	public void contextLoads() {

		PersonGenerator personGenerator = new PersonGenerator();
		Person generate = personGenerator.generate(1000, 10);

		System.out.println(generate);
	}

}
