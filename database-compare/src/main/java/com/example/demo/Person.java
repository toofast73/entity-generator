package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Document(indexName="documents")
@Table
public class Person {

    @Id
    @PrimaryKey
    private String id;
    private Boolean citizen;
    private Double salary;
    private String name;
    private Date dateOfBirth;
    private BigDecimal age;
    private Map<String, Person> friends;

    public Person(double salary, String name) {
        this.salary = salary;
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    public Person() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isCitizen() {
        return citizen;
    }

    public void setCitizen(boolean citizen) {
        this.citizen = citizen;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public BigDecimal getAge() {
        return age;
    }

    public void setAge(BigDecimal age) {
        this.age = age;
    }

    public Boolean getCitizen() {
        return citizen;
    }

    public void setCitizen(Boolean citizen) {
        this.citizen = citizen;
    }

    public Map<String, Person> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Person> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("citizen", citizen)
                .add("salary", salary)
                .add("name", name)
                .add("dateOfBirth", dateOfBirth)
                .add("age", age)
                .toString();
    }
}
