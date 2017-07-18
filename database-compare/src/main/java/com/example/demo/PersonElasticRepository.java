package com.example.demo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;

public interface PersonElasticRepository extends ElasticsearchRepository<Person, String> {

    Long countByName(String name);

    Collection<Person> findByName(String name);

}
