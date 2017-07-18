package com.example.demo;

import com.google.common.base.Stopwatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/**
	 * Callback used to run the bean.
	 *
	 * @param args incoming main method arguments
	 * @throws Exception on error
	 */
	@Autowired
	PersonMongoRepository repository;


	@Override
	public void run(String... args) throws Exception {
		repository.deleteAll();

		// save a couple of customers
		List<Person> persons = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			Person person = new Person(100, "Smith");
			Person firstFriend  = new Person(100, "F1");
			Person secondFriend = new Person(100, "F2");
			Map<String, Person> friendMap = new HashMap<>();
			friendMap.put(firstFriend.getId(), firstFriend);
			friendMap.put(secondFriend.getId(), secondFriend);
			person.setFriends(friendMap);
			persons.add(person);
		}

		List<String> ids = new ArrayList<>();
		for (Person person : persons) {
			ids.add(person.getId());
		}


		Stopwatch started = Stopwatch.createStarted();

		repository.save(persons);



		System.err.println("ELAPSED: " + started.stop().elapsed(TimeUnit.MILLISECONDS));

		Stopwatch reads = Stopwatch.createStarted();
		for (String id : ids) {
			Person one = repository.findOne(id);
		}
		System.err.println("READS:" + reads.stop().elapsed(TimeUnit.MILLISECONDS));



	}
}
