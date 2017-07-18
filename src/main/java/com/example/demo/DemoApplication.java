package com.example.demo;

import com.example.data.filereader.PojoLoader;
import com.example.data.pojo.Operation;
import com.google.common.base.Stopwatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

@SpringBootApplication(
		scanBasePackages = {"com.example"}
)
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

	@Autowired
	PojoLoader pojoLoader;

	@Override
	public void run(String... args) throws Exception {
		repository.deleteAll();

		Operation result = new Operation();
		result.setAge(1);
		result.setAbout("About");

		List<Operation[]> operations = pojoLoader.loadAll();
		List<Operation> operationList = new ArrayList<>();

		for (Operation[] operation : operations) {
			operationList.addAll(newArrayList(operation));
		}
		result.setNestedOperations(operationList);

		Stopwatch started = Stopwatch.createStarted();

		repository.save(result);
		System.err.println("ELAPSED" + started.stop().elapsed(TimeUnit.MILLISECONDS));
		System.err.println("COUNT" + repository.count());

	}
}
