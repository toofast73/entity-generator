package com.example.dao.mongo;

import com.example.data.pojo.Operation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface PersonMongoRepository extends MongoRepository<Operation, String> {

    Long countByName(String name);

    Collection<Operation> findByName(String name);

}
