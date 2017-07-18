package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface PersonMongoRepository extends MongoRepository<Person, String> {

    Long countByName(String name);

    Collection<Person> findByName(String name);

}
