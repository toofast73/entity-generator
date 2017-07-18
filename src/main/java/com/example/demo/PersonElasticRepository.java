package com.example.demo;

import com.example.data.pojo.Operation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;

public interface PersonElasticRepository extends ElasticsearchRepository<Operation, String> {

    Long countByName(String name);

    Collection<Operation> findByName(String name);

}
